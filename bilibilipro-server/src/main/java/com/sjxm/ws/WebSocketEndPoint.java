package com.sjxm.ws;

import com.alibaba.fastjson.JSON;
import com.sjxm.dto.MessageDTO;
import com.sjxm.entity.Message;
import com.sjxm.mapper.MessageMapper;
import com.sjxm.service.*;
import com.sjxm.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/{uid}")
@Component
@Slf4j
public class WebSocketEndPoint {


    private static final Map<Long,Session> onlineUsers = new ConcurrentHashMap<>();
    private Long uid;

    private static MessageService messageService;

    private static AccountService accountService;

    private static VideoService videoService;

    private static CommentService commentService;

    private static DynamicService dynamicService;

    private static MessageMapper messageMapper;


    @Autowired
    public void setMessageService(MessageService messageService){
        WebSocketEndPoint.messageService = messageService;
    }

    @Autowired
    public void setAccountService(AccountService accountService){
        WebSocketEndPoint.accountService = accountService;
    }

    @Autowired
    public void setVideoService(VideoService videoService){
        WebSocketEndPoint.videoService = videoService;
    }

    @Autowired
    public void setCommentService(CommentService commentService){
        WebSocketEndPoint.commentService = commentService;
    }

    @Autowired
    public void setDynamicService(DynamicService dynamicService){
        WebSocketEndPoint.dynamicService = dynamicService;
    }

    @Autowired
    public void setMessageMapper(MessageMapper messageMapper){WebSocketEndPoint.messageMapper = messageMapper;}




    /**
     * 建立websocket连接后被调用
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") Long uid){
        log.info("用户{}建立连接",uid);
        this.uid = uid;
        //将session进行保存
        onlineUsers.put(uid,session);

    }

    @OnMessage
    public void onMessage(String message) throws IOException {

        log.info("获取到了消息：{}",message);
        MessageDTO messageDTO = JSON.parseObject(message, MessageDTO.class);
        char type = messageDTO.getType();
        Long fromUid = messageDTO.getFromUid();
        Long toUid = messageDTO.getToUid();
        char isSystem = messageDTO.getIsSystem();
        if(isSystem == '1')
        {
            //系统消息
        }
        else{
            //普通消息
            if(type == '0'){
                //私信内容
                if(messageDTO.isAll()){
                    //获取历史聊天信息
                    List<MessageVO> list = messageService.getHistoryMessage(fromUid, toUid);
                    for (MessageVO messageVO : list) {
                        messageVO.setIsAll(true);
                        sendSpecificUserMsg(JSON.toJSONString(messageVO),fromUid);
                    }
                }
                else{
                    //发送私信
                    Message messageTmp = new Message();
                    BeanUtils.copyProperties(messageDTO,messageTmp);
                    messageTmp.setIsRead('0');
                    messageTmp.setPostTime(LocalDateTime.now());
                    messageMapper.addMessage(messageTmp);
                    MessageVO messageVO = new MessageVO();
                    BeanUtils.copyProperties(messageTmp,messageVO);
                    MessageVO messageVOTmp = getUserInfo(messageVO);
                    messageVOTmp.setIsAll(false);
                    sendSpecificUserMsg(JSON.toJSONString(messageVOTmp),toUid);
                }
            }
            else if(type == '1'){
                //点赞信息
                if(messageDTO.isAll()){
                    //获取历史点赞信息
                    List<MessageVO> list = messageService.getLikeMessage(fromUid);
                    for (MessageVO messageVO : list) {
                        messageVO.setIsAll(true);
                        sendSpecificUserMsg(JSON.toJSONString(messageVO),fromUid);
                    }
                }
                else{
                    Message messageTmp = new Message();
                    BeanUtils.copyProperties(messageDTO,messageTmp);
                    messageTmp.setIsRead('0');
                    messageTmp.setPostTime(LocalDateTime.now());
                    messageMapper.addMessage(messageTmp);
                    MessageVO messageVO = new MessageVO();
                    BeanUtils.copyProperties(messageTmp,messageVO);
                    MessageVO messageVOTmp = getUserInfo(messageVO);
                    messageVOTmp = perfectOtherInfo(messageVOTmp);
                    messageVOTmp.setIsAll(false);
                    sendSpecificUserMsg(JSON.toJSONString(messageVOTmp),toUid);
                }
            }
            else if(type == '3'){
                //评论信息
                if(messageDTO.isAll()){
                    //获取历史回复信息
                    List<MessageVO> list = messageService.getCommentMessage(fromUid);
                    for (MessageVO messageVO : list) {
                        messageVO.setIsAll(true);
                        sendSpecificUserMsg(JSON.toJSONString(messageVO),fromUid);
                    }
                }
                else{
                    Message messageTmp = new Message();
                    BeanUtils.copyProperties(messageDTO,messageTmp);
                    messageTmp.setIsRead('0');
                    messageTmp.setPostTime(LocalDateTime.now());
                    messageMapper.addMessage(messageTmp);
                    MessageVO messageVO = new MessageVO();
                    BeanUtils.copyProperties(messageTmp,messageVO);
                    MessageVO messageVOTmp = getUserInfo(messageVO);
                    messageVOTmp = perfectOtherInfo(messageVOTmp);
                    messageVOTmp.setIsAll(false);
                    Long nowCommentId = messageVOTmp.getNowCommentId();
                    if(nowCommentId!=null){
                        messageVOTmp.setContent(commentService.info(nowCommentId).getContent());
                    }
                    sendSpecificUserMsg(JSON.toJSONString(messageVOTmp),toUid);
                }
            }
        }

    }

    private MessageVO perfectOtherInfo(MessageVO messageVOTmp) {
        Long videoId = messageVOTmp.getVideoId();
        Long commentId = messageVOTmp.getCommentId();
        Long dynamicId = messageVOTmp.getDynamicId();
        if(videoId!=null){
            VideoVO videoVO = videoService.info(videoId);
            messageVOTmp.setVideoPoster(videoVO.getPoster());
            return messageVOTmp;
        }
        else if(commentId!=null){
            CommentVO commentVO = commentService.info(commentId);
            messageVOTmp.setCommentContent(commentVO.getContent());
            return messageVOTmp;
        }
        else if(dynamicId!=null){
            DynamicVO dynamicVO = dynamicService.info(dynamicId);
            messageVOTmp.setDynamicText(dynamicVO.getText());
            return messageVOTmp;
        }
        return messageVOTmp;
    }

    /**
     * 关闭websocket连接时该方法被自动调用
     * @param session
     */
    @OnClose
    public void onClose(Session session){

        log.info("用户{}下线了",this.uid);
        onlineUsers.remove(uid);
    }

    /**
     * 把消息广播给所有的用户
     * @param message
     */
    private void broadcastAllUsers(String message) throws IOException {

        Set<Map.Entry<Long,Session>> entries = onlineUsers.entrySet();
        for (Map.Entry<Long, Session> entry : entries) {
            //获取到所有用户对应的session对象
            Session session = entry.getValue();
            //发送消息
            session.getBasicRemote().sendText(message);
        }

    }

    private void sendSpecificUserMsg(String message,Long uid) throws IOException {
        Session session = onlineUsers.get(uid);
        if(session != null){
            session.getBasicRemote().sendText(message);
        }
        //如果session不存在，即用户下线时，将用户接收到到信息存储到一个临时变量中，等待用户上线时把这个变量中所有数据进行发送
    }



    private MessageVO getUserInfo(MessageVO messageVO){
        Long fromUidTmp = messageVO.getFromUid();
        Long toUidTmp = messageVO.getToUid();
        if(fromUidTmp!=null){
            AccountVO fromAccountVO = accountService.info(fromUidTmp);
            messageVO.setFromName(fromAccountVO.getAccountName());
            messageVO.setFromAvatar(fromAccountVO.getAvatar());
        }
        if(toUidTmp != null){
            AccountVO toAccountVO = accountService.info(toUidTmp);
            messageVO.setToName(toAccountVO.getAccountName());
            messageVO.setToAvatar(toAccountVO.getAvatar());
        }
        return messageVO;
    }

    /**
     * 将消息存储到数据库或消息队列
     *
     * @param messageDTO
     */
//    private void saveMessage(MessageDTO messageDTO) {
//        // 实现消息存储逻辑，可以存储到数据库或消息队列
//        Message message = Message.builder()
//                .content(messageDTO.getContent())
//                .postId(this.userId)
//                .receiveId(messageDTO.getReceiveId())
//                .postTime(LocalDateTime.now())
//                .build();
//        messageMapper.saveMessage(message);
//    }

    /**
     * 发送离线消息给用户
     *
     * @param userId  用户ID
     * @param session WebSocket会话
     */
//    private void sendOfflineMessages(Long userId, Session session) throws IOException {
//        // 实现离线消息获取逻辑，从数据库或消息队列中获取离线消息
//        List<Message> offlineMessages = messageMapper.getMessageList(userId);
//
//        // 循环发送离线消息
//        for (Message message : offlineMessages) {
//            MessageVO messageVO = new MessageVO();
//            BeanUtils.copyProperties(message,messageVO);
//            session.getBasicRemote().sendText(JSON.toJSONString(messageVO));
//        }
//    }
}
