package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoPageDTO implements Serializable {

    private Integer page;

    private Integer pageSize;

    private Integer order;

    private String key;
}
