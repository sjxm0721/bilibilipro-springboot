package com.sjxm.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowVO implements Serializable {

    private Long followId;

    private Long followerUid;

    private String followerName;

    private String followerAvatar;

    private String followerBrief;

    private Long followedUid;

    private String followedName;

    private String followedAvatar;

    private String followedBrief;

    private LocalDateTime createTime;
}
