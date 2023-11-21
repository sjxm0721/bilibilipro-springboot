package com.sjxm.service.impl;

import com.sjxm.service.RedisService;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.VideoVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {


    @Autowired
    private RedisUtil redisUtil;

    /**
     * 视频信息存储入redis
     * @param videoVO
     */
    @Override
    public void saveVideo2Redis(VideoVO videoVO) {
        Long videoId = videoVO.getVideoId();
        String redisKey = "video:videoId="+videoId;
        redisUtil.set(redisKey,videoVO);
    }

    /**
     * 删除redis中的视频信息
     * @param videoId
     */
    @Override
    public void deleteVideoFromRedis(Long videoId) {
        String redisKey = "video:videoId="+videoId;
        redisUtil.del(redisKey);
    }

    /**
     * 获取redis中所有的视频信息
     */
    @Override
    public void getVideoDataFromRedis() {

    }
}
