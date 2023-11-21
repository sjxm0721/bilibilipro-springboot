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
public class Dynamic implements Serializable {

    private Long dynamicId;

    private Long uid;

    private LocalDateTime postTime;

    private String text;

    private Long videoId;

    private Integer likeNum;

    private Integer commentNum;


}
