package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FollowDTO implements Serializable {

    private Long followerUid;

    private Long followedUid;

}
