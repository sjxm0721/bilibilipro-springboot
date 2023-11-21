package com.sjxm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarrageVO implements Serializable {

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
