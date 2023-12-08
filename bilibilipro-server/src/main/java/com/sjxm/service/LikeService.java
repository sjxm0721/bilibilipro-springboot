package com.sjxm.service;

import com.sjxm.dto.LikeDTO;
import com.sjxm.result.PageResult;
import com.sjxm.vo.LikeVO;

import java.util.List;

public interface LikeService {
    List<LikeVO> list(Long uid,char type);

    void add(LikeDTO likeDTO);

    void cancel(LikeDTO likeDTO);

    void transLikeFromRedis2DB();

    PageResult videoPage(Long uid, Integer page, Integer pageSize);
}
