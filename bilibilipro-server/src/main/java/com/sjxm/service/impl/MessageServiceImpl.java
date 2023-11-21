package com.sjxm.service.impl;

import com.sjxm.constant.MessageConstant;
import com.sjxm.constant.RedisExpireConstant;
import com.sjxm.entity.Message;
import com.sjxm.entity.MessageResult;
import com.sjxm.exception.MessageNotFoundException;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.MessageMapper;
import com.sjxm.service.*;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private VideoService videoService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DynamicService dynamicService;



    /**
     * 根据用户uid获取该用户最近的聊天列表
     * @param uid
     * @return
     */
    @Override
    public List<AccountVO> userlist(Long uid) {

        Comparator<MessageResult> mapComparator = new Comparator<MessageResult>() {
            @Override
            public int compare(MessageResult messageResul1, MessageResult messageResult2) {
                LocalDateTime dateTime1 = messageResul1.getLatestPostTime();
                LocalDateTime dateTime2 = messageResult2.getLatestPostTime();
                return dateTime2.compareTo(dateTime1);
            }
        };

        List<MessageResult> uidList1 = messageMapper.getLatestCommuUser1(uid);
        List<MessageResult> uidList2 = messageMapper.getLatestCommuUser2(uid);
        List<MessageResult> uidListTmp = new ArrayList<>(uidList1);
        uidListTmp.addAll(uidList2);

        Collections.sort(uidListTmp,mapComparator);

        List<Long> uidList = uidListTmp.stream()
                .map(MessageResult::getUid)
                .distinct()
                .collect(Collectors.toList());


        List<AccountVO> list = new ArrayList<>();

        for (Long uidTmp : uidList) {
            AccountVO accountVO = accountService.info(uidTmp);
            list.add(accountVO);
        }
        return list;
    }

    /**
     * 批量设置消息为已读
     * @param messageIds
     */
    @Override
    public void setRead(List<Long> messageIds) {
        if(messageIds!=null&&messageIds.size()>0){
            for (Long messageId : messageIds) {
                MessageVO messageVO = info(messageId);
                messageVO.setIsRead('1');
                update(messageVO);
            }
        }
    }

    /**
     * 获取message信息
     * @param messageId
     * @return
     */
    @Override
    public MessageVO info(Long messageId) {
        String redisKey = "message:messageId="+messageId;
        Object o = redisUtil.get(redisKey);
        if(o!=null){
            JacksonObjectMapper om = new JacksonObjectMapper();
            return om.convertValue(o, MessageVO.class);
        }
        else{
            Message message = messageMapper.getByMessageId(messageId);
            if(message == null){
                throw new MessageNotFoundException(MessageConstant.MESSAGE_NOT_FOUND);
            }
            MessageVO messageVO = new MessageVO();
            BeanUtils.copyProperties(message,messageVO);
            Long videoId = messageVO.getVideoId();
            Long commentId = messageVO.getCommentId();
            Long dynamicId = messageVO.getDynamicId();
            Long fromUid = messageVO.getFromUid();
            Long toUid = messageVO.getToUid();
            Long nowCommentId = messageVO.getNowCommentId();
            if(fromUid!=null){
                AccountVO fromAccountVO = accountService.info(fromUid);
                messageVO.setFromName(fromAccountVO.getAccountName());
                messageVO.setFromAvatar(fromAccountVO.getAvatar());
            }
            if(toUid!=null){
                AccountVO toAccountVO = accountService.info(toUid);
                messageVO.setToName(toAccountVO.getAccountName());
                messageVO.setToAvatar(toAccountVO.getAvatar());
            }
            if(nowCommentId!=null){
                CommentVO nowCommentVO = commentService.info(nowCommentId);
                messageVO.setContent(nowCommentVO.getContent());
            }
            if(videoId!=null){
                VideoVO videoVO = videoService.info(videoId);
                messageVO.setVideoPoster(videoVO.getPoster());
            }
            else if(commentId!=null){
                CommentVO commentVO = commentService.info(commentId);
                messageVO.setCommentContent(commentVO.getContent());
            }
            else if(dynamicId!=null){
                DynamicVO dynamicVO = dynamicService.info(dynamicId);
                messageVO.setDynamicText(dynamicVO.getText());
            }
            redisUtil.set(redisKey,messageVO);
            return messageVO;
        }
    }

    /**
     * 更新message信息
     * @param messageVO
     */
    @Override
    public void update(MessageVO messageVO) {
        String redisKey = "message:messageId="+messageVO.getMessageId();
        redisUtil.set(redisKey,messageVO);
    }

    /**
     * 获取历史聊天记录
     * @param fromUid
     * @param toUid
     * @return
     */
    @Override
    public List<MessageVO> getHistoryMessage(Long fromUid, Long toUid) {
        List<Message> historyMessage = messageMapper.getHistoryMessage(fromUid, toUid);
        List<MessageVO> list = new ArrayList<>();
        for (Message message : historyMessage) {
            MessageVO messageVO = info(message.getMessageId());
            list.add(messageVO);
        }
        return list;
    }

    /**
     * 获取点赞信息列表
     * @param uid
     * @return
     */
    @Override
    public List<MessageVO> getLikeMessage(Long uid) {
        List<Message> likeMessage = messageMapper.getLikeMessage(uid);
        List<MessageVO> list = new ArrayList<>();
        for (Message message : likeMessage) {
            MessageVO messageVO = info(message.getMessageId());
            list.add(messageVO);
        }
        return list;
    }

    /**
     * 获取回复信息列表
     * @param uid
     * @return
     */
    @Override
    public List<MessageVO> getCommentMessage(Long uid) {
        List<Message> commentMessage = messageMapper.getCommentMessage(uid);
        List<MessageVO> list = new ArrayList<>();
        for (Message message : commentMessage) {
            MessageVO messageVO = info(message.getMessageId());
            list.add(messageVO);
        }
        return list;
    }

    /**
     * 将redis中的message数据存储入数据库
     */
    @Override
    public void transMessageFromRedis2DB() {
        Set<String> keys = redisUtil.keys("message:messageId=" + '*');
        JacksonObjectMapper om = new JacksonObjectMapper();
        if (keys != null) {
            for (String key : keys) {
                Object o = redisUtil.get(key);
                MessageVO messageVO = om.convertValue(o, MessageVO.class);
                Message message = new Message();
                BeanUtils.copyProperties(messageVO,message);
                messageMapper.updateMessage(message);
                redisUtil.del(key);
            }
        }
    }
}
