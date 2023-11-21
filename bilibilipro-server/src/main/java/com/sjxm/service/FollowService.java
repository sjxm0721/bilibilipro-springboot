package com.sjxm.service;

import com.sjxm.dto.FollowDTO;
import com.sjxm.result.PageResult;

public interface FollowService {
    PageResult follow(Integer page, Integer pageSize, Long uid);

    PageResult fans(Integer page, Integer pageSize, Long uid);

    void add(FollowDTO followDTO);

    Boolean isfollow(Long followerUid, Long followedUid);

    void cancel(FollowDTO followDTO);
}
