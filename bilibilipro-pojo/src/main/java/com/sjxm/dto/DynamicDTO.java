package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DynamicDTO implements Serializable {
    private Long uid;
    private String text;
    private Long videoId;
}
