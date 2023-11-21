package com.sjxm.entity;

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
public class Follow implements Serializable {

    private Long followId;

    private Long followerUid;

    private Long followedUid;

    private LocalDateTime createTime;

}
