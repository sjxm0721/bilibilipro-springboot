package com.sjxm.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sjxm.constant.MessageConstant;
import com.sjxm.dto.DynamicDTO;
import com.sjxm.entity.Account;
import com.sjxm.entity.Dynamic;
import com.sjxm.entity.Video;
import com.sjxm.exception.AccountNotFoundException;
import com.sjxm.exception.DynamicNotFoundException;
import com.sjxm.exception.VideoNotFoundException;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.DynamicMapper;
import com.sjxm.result.PageResult;
import com.sjxm.service.AccountService;
import com.sjxm.service.CommentService;
import com.sjxm.service.DynamicService;
import com.sjxm.service.VideoService;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.AccountVO;
import com.sjxm.vo.DynamicVO;
import com.sjxm.vo.VideoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DynamicServiceImpl implements DynamicService {

    @Autowired
    private DynamicMapper dynamicMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CommentService commentService;


    /**
     * 获取动态分页数据
     * @param page
     * @param pageSize
     * @param uid
     * @return
     */
    @Override
    public PageResult page(Integer page,Integer pageSize,Long uid) {

        PageHelper.startPage(page,pageSize,"post_time desc");

        Page<Dynamic> pageResult = dynamicMapper.getDynamicByUid(uid);

        long total = pageResult.getTotal();

        List<Dynamic> list = pageResult.getResult();

        List<DynamicVO> result = new ArrayList<>();

        for (Dynamic dynamic : list) {
            Long dynamicId = dynamic.getDynamicId();
            DynamicVO dynamicVO = info(dynamicId);
            result.add(dynamicVO);
        }

        return new PageResult(total,result);
    }

    /**
     * 更新dynamicVO信息
     * @param dynamicVO
     */
    @Override
    public void update(DynamicVO dynamicVO) {
        Dynamic dynamic = new Dynamic();
        BeanUtils.copyProperties(dynamicVO,dynamic);
        dynamicMapper.updateDynamic(dynamic);
        cleanCache(dynamicVO.getDynamicId());
    }

    /**
     * 获取dynamicVO的信息
     * @param dynamicId
     * @return
     */
    @Override
    public DynamicVO info(Long dynamicId) {

        String redisKey = "dynamic:dynamicId="+dynamicId;

        Object o = redisUtil.get(redisKey);
        JacksonObjectMapper om = new JacksonObjectMapper();
        if(o!=null){
            return om.convertValue(o,DynamicVO.class);
        }
        else{
            Dynamic dynamic = dynamicMapper.getDynamicById(dynamicId);
            if(dynamic == null){
                throw new DynamicNotFoundException(MessageConstant.DYNAMIC_NOT_FOUND);
            }
            DynamicVO dynamicVO = new DynamicVO();
            BeanUtils.copyProperties(dynamic,dynamicVO);
            Long uid = dynamicVO.getUid();
            Long videoId = dynamicVO.getVideoId();
            AccountVO accountVO = accountService.info(uid);
            dynamicVO.setAvatar(accountVO.getAvatar());
            dynamicVO.setAccountName(accountVO.getAccountName());
            if(videoId!=null){
                VideoVO videoVO = videoService.info(videoId);
                dynamicVO.setPoster(videoVO.getPoster());
                dynamicVO.setLastTime(videoVO.getLastTime());
                dynamicVO.setTitle(videoVO.getTitle());
                dynamicVO.setClickNum(videoVO.getClickNum());
                dynamicVO.setBarrageNum(videoVO.getBarrageNum());
                dynamicVO.setVideoBrief(videoVO.getVideoBrief());
            }
            return dynamicVO;
        }
    }

    /**
     * 发表评论
     * @param dynamicDTO
     */
    @Override
    @Transactional
    public void add(DynamicDTO dynamicDTO) {
        Dynamic dynamic = new Dynamic();
        BeanUtils.copyProperties(dynamicDTO,dynamic);
        dynamic.setPostTime(LocalDateTime.now());
        dynamicMapper.addDynamic(dynamic);
        AccountVO accountVO = accountService.info(dynamic.getUid());
        accountVO.setDynamicNum(accountVO.getDynamicNum()+1);
        accountService.update(accountVO);
    }

    /**
     * 删除动态
     * @param dynamicId
     */
    @Override
    @Transactional
    public void delete(Long dynamicId) {
        Dynamic dynamic = dynamicMapper.getDynamicById(dynamicId);
        Long uid = dynamic.getUid();
        AccountVO accountVO = accountService.info(uid);
        accountVO.setDynamicNum(accountVO.getDynamicNum()-1);
        accountService.update(accountVO);
        commentService.deleteByDynamicId(dynamicId);
        dynamicMapper.deleteDynamic(dynamicId);
        cleanCache(dynamicId);

    }

    public void cleanCache(Long dynamicId){
        String redisKey = "dynamic:dynamicId="+dynamicId;
        redisUtil.del(redisKey);
    }
}
