package com.sjxm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

//弹幕
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Barrage implements Serializable {

    private Long barrageId;

    private Double time;

    private Long uid;

    private Integer likeNum;

    private String text;

    private LocalDateTime postTime;

    private Long videoId;

    private String type;

    private String color;


}
