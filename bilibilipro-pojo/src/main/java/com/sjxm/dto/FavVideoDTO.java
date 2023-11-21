package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FavVideoDTO implements Serializable {

    private Long uid;

    private Long videoId;

    private Long fatherDic;
}
