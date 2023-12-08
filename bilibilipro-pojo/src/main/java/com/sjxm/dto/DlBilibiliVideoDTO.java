package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DlBilibiliVideoDTO implements Serializable {

    private String videoType;

    private String bid;

    private Integer pnum;

    private String sessData;

}
