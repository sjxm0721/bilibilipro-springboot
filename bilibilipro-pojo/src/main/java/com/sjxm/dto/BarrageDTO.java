package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BarrageDTO implements Serializable {

    private Double time;

    private Long uid;

    private String text;

    private Long videoId;

    private String type;

    private String color;

}
