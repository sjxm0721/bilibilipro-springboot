package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoDTO implements Serializable {
    private String title;

    private String src;

    private Integer lastTime;

    private Long uid;

    private String videoBrief;

    private Long[] tags;

    private String poster;

}
