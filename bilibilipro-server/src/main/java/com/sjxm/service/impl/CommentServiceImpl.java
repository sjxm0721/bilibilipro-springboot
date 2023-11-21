package com.sjxm.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sjxm.constant.MessageConstant;
import com.sjxm.dto.CommentDTO;
import com.sjxm.dto.MessageDTO;
import com.sjxm.entity.Comment;
import com.sjxm.exception.CommentNotFoundException;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.CommentMapper;
import com.sjxm.result.PageResult;
import com.sjxm.service.AccountService;
import com.sjxm.service.CommentService;
import com.sjxm.service.DynamicService;
import com.sjxm.service.VideoService;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.AccountVO;
import com.sjxm.vo.CommentVO;
import com.sjxm.vo.DynamicVO;
import com.sjxm.vo.VideoVO;
import com.sjxm.ws.WebSocketEndPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {


    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private VideoService videoService;

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AccountService accountService;

    /**
     * 获取评论分页列表数据
     * @param page
     * @param pageSize
     * @param order
     * @param dynamicId
     * @param videoId
     * @return
     */
    @Override
    public PageResult page(Integer page, Integer pageSize, Integer order, Integer dynamicId, Integer videoId) {

        List<String> orderChosed = new ArrayList<>();
        orderChosed.add("like_num desc");
        orderChosed.add("post_time desc");


            //从数据库获取数据
            PageHelper.startPage(page,pageSize,orderChosed.get(order));
            Page<Comment> pageResult = commentMapper.getCommentPageList(videoId,dynamicId);

            List<Comment> list = pageResult.getResult();
            long total = pageResult.getTotal();

            List<CommentVO> result = new ArrayList<>();

            for (Comment comment : list) {
                Long commentId = comment.getCommentId();
                CommentVO commentVO = info(commentId);
                result.add(commentVO);
            }


        return new PageResult(total,result);
    }
    /**
     * 用户发表评论
     * @param commentDTO
     */
    @Override
    public Long reply(CommentDTO commentDTO) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO,comment);
        comment.setPostTime(LocalDateTime.now());
        commentMapper.publishComment(comment);
        Long videoId = comment.getVideoId();
        Long dynamicId = comment.getDynamicId();
        Long totalFatherId = comment.getTotalFatherId();
        if(videoId!=null){
            VideoVO videoVO = videoService.info(videoId);
            videoVO.setCommentNum(videoVO.getCommentNum()+1);
            videoService.update(videoVO);
        }
        else if(dynamicId!=null){
            DynamicVO dynamicVO = dynamicService.info(dynamicId);
            dynamicVO.setCommentNum(dynamicVO.getCommentNum()+1);
            dynamicService.update(dynamicVO);
        }
        else if(totalFatherId!=null){
            redisUtil.del("comment:commentId="+totalFatherId);
        }
        return comment.getCommentId();
    }

    @Override
    public CommentVO info(Long commentId) {
        String redisKey = "comment:commentId="+commentId;
        Object o = redisUtil.get(redisKey);
        JacksonObjectMapper om = new JacksonObjectMapper();
        CommentVO commentVO = null;
        if(o!=null){
            commentVO = om.convertValue(o,CommentVO.class);
        }
        else{
            Comment comment = commentMapper.getCommentById(commentId);
            if(comment == null){
                throw new CommentNotFoundException(MessageConstant.COMMENT_NOT_FOUND);
            }
            commentVO = new CommentVO();
            BeanUtils.copyProperties(comment,commentVO);
            AccountVO accountVO = accountService.info(commentVO.getUid());
            commentVO.setAvatar(accountVO.getAvatar());
            commentVO.setAccountName(accountVO.getAccountName());
        }
            
            //完善用户信息
            if(commentVO.getTotalFatherId()==null){
                //针对父评论
                Long totalFatherCommentId = commentVO.getCommentId();
                List<Comment> childrenList = commentMapper.getChildrenCommentList(totalFatherCommentId);
                List<CommentVO> childrenResult = new ArrayList<>();

                for (Comment childrenComment : childrenList) {
                    CommentVO childrenCommentVO = info(childrenComment.getCommentId());
                    Long fatherCommentId = childrenCommentVO.getFatherId();
                    //1.针对总父评的子评的回复
                    if(!Objects.equals(fatherCommentId, totalFatherCommentId)){
                        CommentVO commentVOTmp = info(fatherCommentId);
                        AccountVO accountVOTmp = accountService.info(commentVOTmp.getUid());
                        childrenCommentVO.setResponseName(accountVOTmp.getAccountName());
                    }
                    childrenResult.add(childrenCommentVO);
                }
                commentVO.setSubComment(childrenResult);
            }
            redisUtil.set(redisKey,commentVO);
            return commentVO;
    }

    @Override
    public void update(CommentVO commentVO) {
        String redisKey = "comment:commentId="+commentVO.getCommentId();
        redisUtil.set(redisKey,commentVO);
    }

    /**
     * 用户删除评论
     * @param commentId
     */
    @Override
    @Transactional
    public void delete(Long commentId) {
        CommentVO commentVO = info(commentId);
        Long totalFatherId = commentVO.getTotalFatherId();
        if(totalFatherId == null){
            //总父评论
            Long videoId = commentVO.getVideoId();
            Long dynamicId = commentVO.getDynamicId();
            if(videoId!=null){
                VideoVO videoVO = videoService.info(videoId);
                videoVO.setCommentNum(videoVO.getCommentNum()-1);
                videoService.update(videoVO);
            }
            else if(dynamicId!=null){
                DynamicVO dynamicVO = dynamicService.info(dynamicId);
                dynamicVO.setCommentNum(dynamicVO.getCommentNum()-1);
                dynamicService.update(dynamicVO);
            }
            List<Comment> childrenCommentList = commentMapper.getChildrenCommentList(commentId);
            if(childrenCommentList!=null && childrenCommentList.size()>0){
                for (Comment comment : childrenCommentList) {
                    deleteComment(comment.getCommentId());
                }
            }
            deleteComment(commentId);
        }
        else{
            //子评论
            List<Comment> ltChildrenCommentList = commentMapper.getLTChildrenCommentList(commentId);
            if(ltChildrenCommentList!=null&&ltChildrenCommentList.size()>0){
                for (Comment comment : ltChildrenCommentList) {
                    deleteComment(comment.getCommentId());
                }
            }
            deleteComment(commentId);
        }
    }

    @Override
    @Transactional
    public void transCommentFromRedis2DB() {
        Set<String> keys = redisUtil.keys("comment:commentId=" + "*");
        JacksonObjectMapper om  = new JacksonObjectMapper();
        for (String key : keys) {
            Object o = redisUtil.get(key);
            CommentVO commentVO = om.convertValue(o, CommentVO.class);
            Comment comment = new Comment();
            BeanUtils.copyProperties(commentVO,comment);
            commentMapper.updateComment(comment);
            redisUtil.del(key);
        }
    }

    /**
     * 删除指定dynamic下的评论数据
     * @param dynamicId
     */
    @Override
    @Transactional
    public void deleteByDynamicId(Long dynamicId) {
        List<Comment> list = commentMapper.getCommentListByDynamicId(dynamicId);
        for (Comment comment : list) {
            Long commentId = comment.getCommentId();
            delete(commentId);
        }
    }


    public void deleteComment(Long commentId){
        String redisKey = "comment:commentId="+commentId;
        commentMapper.deleteComment(commentId);
        redisUtil.del(redisKey);
    }
}
