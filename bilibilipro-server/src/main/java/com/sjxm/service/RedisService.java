package com.sjxm.service;

import com.sjxm.vo.VideoVO;

public interface RedisService {

    //视频信息存储入redis
    void saveVideo2Redis(VideoVO videoVO);
    //从redis中删除视频信息
    void deleteVideoFromRedis(Long videoId);
    //从redis中获取所有的视频信息
    void getVideoDataFromRedis();

}
