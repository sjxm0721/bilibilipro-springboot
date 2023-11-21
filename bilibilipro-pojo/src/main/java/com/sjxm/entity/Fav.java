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
public class Fav implements Serializable {

    private Long favId;

    private Long uid;

    private char isDic;

    private Long videoId;

    private Long fatherDic;

    private String favTitle;

    private Integer favNum;

    private char isPublic;

    private String favPoster;

    private LocalDateTime createTime;
}
