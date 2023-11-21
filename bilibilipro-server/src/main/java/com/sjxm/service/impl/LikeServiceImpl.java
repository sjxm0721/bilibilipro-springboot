package com.sjxm.service.impl;

import com.sjxm.dto.LikeDTO;
import com.sjxm.entity.Like;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.LikeMapper;
import com.sjxm.service.*;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.*;
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
public class LikeServiceImpl implements LikeService {


    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AccountService accountService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BarrageService barrageService;


    /**
     * 获取该用户点赞数据列表
     * @param uid
     * @return
     */
    @Override
    public List<LikeVO> list(Long uid,char type) {

        String redisKey = "like:uid="+uid;
        List<LikeVO> likeVOList = new ArrayList<>();
        JacksonObjectMapper om = new JacksonObjectMapper();

        if(type == '0'){
            //视频
            redisKey += "&video";
        }
        else if(type == '1'){
            //动态
            redisKey += "&dynamic";
        }
        else if(type=='2'){
            //评论
            redisKey += "&comment";
        }
        else{
            //弹幕
            redisKey += "&barrage";
        }
        Set<Object> objects = redisUtil.sGet(redisKey);
        if (objects != null && !objects.isEmpty()) {
            for (Object object : objects) {
                LikeVO likeVO = om.convertValue(object, LikeVO.class);
                likeVOList.add(likeVO);
            }
        }
        else{
            List<Like> list = likeMapper.getLikeList(uid,type);
            for (Like like : list) {
                LikeVO likeVO = new LikeVO();
                BeanUtils.copyProperties(like,likeVO);
                likeVOList.add(likeVO);
                redisUtil.sSet(redisKey,likeVO);
            }
        }
        return likeVOList;
    }

    /**
     * 用户点赞
     * @param likeDTO
     */
    @Override
    @Transactional
    public void add(LikeDTO likeDTO) {
        Long fromUid = likeDTO.getFromUid();
        char type = likeDTO.getType();
        JacksonObjectMapper om = new JacksonObjectMapper();
        Long toUid = likeDTO.getToUid();
        AccountVO accountVO = accountService.info(toUid);
        accountVO.setLikeNum(accountVO.getLikeNum()+1);
        accountService.update(accountVO);
        Long likeId = null;

        String redisKey = "like:uid="+fromUid;
        if(type == '0'){
            redisKey+= "&video";
            Long videoId = likeDTO.getVideoId();
            VideoVO videoVO = videoService.info(videoId);
            videoVO.setLikeNum(videoVO.getLikeNum()+1);
            videoService.update(videoVO);
            list(fromUid,'0');
            Set<Object> objects = redisUtil.sGet(redisKey);
            if (objects != null) {
                for (Object object : objects) {
                    LikeVO likeVO = om.convertValue(object, LikeVO.class);
                    Long voVideoId = likeVO.getVideoId();
                    if(Objects.equals(voVideoId, videoId)){
                        likeId = likeVO.getLikeId();
                        redisUtil.setRemove(redisKey,object);
                        break;
                    }
                }
            }
            LikeVO likeVO = new LikeVO();
            BeanUtils.copyProperties(likeDTO,likeVO);
            likeVO.setCreateTime(LocalDateTime.now());
            likeVO.setUpdateTime(LocalDateTime.now());
            likeVO.setLikeId(likeId);
            redisUtil.sSet(redisKey,likeVO);

        }
        else if(type == '1'){
            redisKey += "&dynamic";
            Long dynamicId = likeDTO.getDynamicId();
            DynamicVO dynamicVO = dynamicService.info(dynamicId);
            dynamicVO.setLikeNum(dynamicVO.getLikeNum()+1);
            dynamicService.update(dynamicVO);
            list(fromUid,'1');
            Set<Object> objects = redisUtil.sGet(redisKey);
            if (objects != null) {
                for (Object object : objects) {
                    LikeVO likeVO = om.convertValue(object, LikeVO.class);
                    Long voDynamicId = likeVO.getDynamicId();
                    if(Objects.equals(voDynamicId, dynamicId)){
                        likeId = likeVO.getLikeId();
                        redisUtil.setRemove(redisKey,object);
                        break;
                    }
            }
            }
            LikeVO likeVO = new LikeVO();
            BeanUtils.copyProperties(likeDTO,likeVO);
            likeVO.setLikeId(likeId);
            likeVO.setCreateTime(LocalDateTime.now());
            likeVO.setUpdateTime(LocalDateTime.now());
            redisUtil.sSet(redisKey,likeVO);
        }
        else if(type == '2'){
            redisKey +="&comment";
            Long commentId = likeDTO.getCommentId();
            CommentVO commentVO = commentService.info(commentId);
            commentVO.setLikeNum(commentVO.getLikeNum()+1);
            commentService.update(commentVO);
            list(fromUid,'2');
            Set<Object> objects = redisUtil.sGet(redisKey);
            if (objects != null) {
                for (Object object : objects) {
                    LikeVO likeVO = om.convertValue(object, LikeVO.class);
                    Long voCommentId = likeVO.getCommentId();
                    if(Objects.equals(voCommentId, commentId)){
                        likeId = likeVO.getLikeId();
                        redisUtil.setRemove(redisKey,object);
                        break;
                    }
            }
            }
            LikeVO likeVO = new LikeVO();
            BeanUtils.copyProperties(likeDTO,likeVO);
            likeVO.setLikeId(likeId);
            likeVO.setCreateTime(LocalDateTime.now());
            likeVO.setUpdateTime(LocalDateTime.now());
            redisUtil.sSet(redisKey,likeVO);
        }
        else{
            redisKey +="&barrage";
            Long barrageId = likeDTO.getBarrageId();
            BarrageVO barrageVO = barrageService.info(barrageId);
            barrageVO.setLikeNum(barrageVO.getLikeNum()+1);
            barrageService.update(barrageVO);
            list(fromUid,'3');
            Set<Object> objects = redisUtil.sGet(redisKey);
            if (objects != null) {
                for (Object object : objects) {
                    LikeVO likeVO = om.convertValue(object, LikeVO.class);
                    Long voBarrageId = likeVO.getBarrageId();
                    if(Objects.equals(voBarrageId, barrageId)){
                        likeId = likeVO.getLikeId();
                        redisUtil.setRemove(redisKey,object);
                        break;
                    }
                }
            }

            LikeVO likeVO = new LikeVO();
            BeanUtils.copyProperties(likeDTO,likeVO);
            likeVO.setLikeId(likeId);
            likeVO.setCreateTime(LocalDateTime.now());
            likeVO.setUpdateTime(LocalDateTime.now());
            redisUtil.sSet(redisKey,likeVO);

        }

    }

    /**
     * 用户取消点赞
     * @param likeDTO
     */
    @Override
    @Transactional
    public void cancel(LikeDTO likeDTO) {
        Long fromUid = likeDTO.getFromUid();
        char type = likeDTO.getType();
        JacksonObjectMapper om = new JacksonObjectMapper();
        Long toUid = likeDTO.getToUid();
        Long likeId = null;
        AccountVO accountVO = accountService.info(toUid);
        accountVO.setLikeNum(accountVO.getLikeNum()-1);
        accountService.update(accountVO);

        String redisKey = "like:uid="+fromUid;
        if(type == '0'){
            redisKey+= "&video";
            Long videoId = likeDTO.getVideoId();
            VideoVO videoVO = videoService.info(videoId);
            videoVO.setLikeNum(videoVO.getLikeNum()-1);
            videoService.update(videoVO);
            list(fromUid,'0');
            Set<Object> objects = redisUtil.sGet(redisKey);
            if (objects != null) {
                for (Object object : objects) {
                    LikeVO likeVO = om.convertValue(object, LikeVO.class);
                    Long voVideoId = likeVO.getVideoId();
                    if(Objects.equals(voVideoId, videoId)){
                        likeId = likeVO.getLikeId();
                        redisUtil.setRemove(redisKey,object);
                        break;
                    }

            }
            }
            LikeVO likeVO = new LikeVO();
            BeanUtils.copyProperties(likeDTO,likeVO);
            likeVO.setLikeId(likeId);
            likeVO.setCreateTime(LocalDateTime.now());
            likeVO.setUpdateTime(LocalDateTime.now());
            redisUtil.sSet(redisKey,likeVO);
        }
        else if(type == '1'){
            redisKey += "&dynamic";
            Long dynamicId = likeDTO.getDynamicId();
            DynamicVO dynamicVO = dynamicService.info(dynamicId);
            dynamicVO.setLikeNum(dynamicVO.getLikeNum()-1);
            dynamicService.update(dynamicVO);
            list(fromUid,'1');
            Set<Object> objects = redisUtil.sGet(redisKey);
            if (objects != null) {
                for (Object object : objects) {
                    LikeVO likeVO = om.convertValue(object, LikeVO.class);
                    Long voDynamicId = likeVO.getDynamicId();
                    if(Objects.equals(voDynamicId, dynamicId)){
                        likeId = likeVO.getLikeId();
                        redisUtil.setRemove(redisKey,object);
                        break;
                    }
                }
            }

            LikeVO likeVO = new LikeVO();
            BeanUtils.copyProperties(likeDTO,likeVO);
            likeVO.setLikeId(likeId);
            likeVO.setCreateTime(LocalDateTime.now());
            likeVO.setUpdateTime(LocalDateTime.now());
            redisUtil.sSet(redisKey,likeVO);
        }
        else if(type == '2'){
            redisKey +="&comment";
            Long commentId = likeDTO.getCommentId();
            CommentVO commentVO = commentService.info(commentId);
            commentVO.setLikeNum(commentVO.getLikeNum()-1);
            commentService.update(commentVO);
            list(fromUid,'2');
            Set<Object> objects = redisUtil.sGet(redisKey);
            if (objects != null) {
                for (Object object : objects) {
                    LikeVO likeVO = om.convertValue(object, LikeVO.class);
                    Long voCommentId = likeVO.getCommentId();
                    if(Objects.equals(voCommentId, commentId)){
                        likeId = likeVO.getLikeId();
                        redisUtil.setRemove(redisKey,object);
                        break;
                    }
                }
            }

            LikeVO likeVO = new LikeVO();
            BeanUtils.copyProperties(likeDTO,likeVO);
            likeVO.setLikeId(likeId);
            likeVO.setCreateTime(LocalDateTime.now());
            likeVO.setUpdateTime(LocalDateTime.now());
            redisUtil.sSet(redisKey,likeVO);
        }
        else{
            redisKey +="&barrage";
            Long barrageId = likeDTO.getBarrageId();
            BarrageVO barrageVO = barrageService.info(barrageId);
            barrageVO.setLikeNum(barrageVO.getLikeNum()-1);
            barrageService.update(barrageVO);
            list(fromUid,'3');
            Set<Object> objects = redisUtil.sGet(redisKey);
            if (objects != null) {
                for (Object object : objects) {
                    LikeVO likeVO = om.convertValue(object, LikeVO.class);
                    Long voBarrageId = likeVO.getBarrageId();
                    if(Objects.equals(voBarrageId, barrageId)){
                        likeId = likeVO.getLikeId();
                        redisUtil.setRemove(redisKey,object);
                        break;
                    }

            }
            }
            LikeVO likeVO = new LikeVO();
            BeanUtils.copyProperties(likeDTO,likeVO);
            likeVO.setLikeId(likeId);
            likeVO.setCreateTime(LocalDateTime.now());
            likeVO.setUpdateTime(LocalDateTime.now());
            redisUtil.sSet(redisKey,likeVO);
        }

    }

    /**
     * 将缓存中like数据存储到数据库
     */
    @Override
    @Transactional
    public void transLikeFromRedis2DB() {
        Set<String> keys = redisUtil.keys("like:uid=" + "*");
        JacksonObjectMapper om = new JacksonObjectMapper();
        for (String key : keys) {
            Set<Object> objects = redisUtil.sGet(key);
            if(objects !=null && !objects.isEmpty())
            {
                for (Object object : objects) {
                    LikeVO likeVO = om.convertValue(object, LikeVO.class);
                    Like like = new Like();
                    BeanUtils.copyProperties(likeVO,like);
                    Long likeId = like.getLikeId();
                    if(likeId!=null){
                        //更新
                        likeMapper.updateLike(like);
                    }
                    else{
                        //插入
                        likeMapper.addLike(like);
                    }
                }
                redisUtil.del(key);
            }
        }
        likeMapper.deleteLikeCanceled();
    }

}
