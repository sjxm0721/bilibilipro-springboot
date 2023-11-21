package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FavDTO implements Serializable {

    private Long uid;

    private char isDic;

    private String favTitle;

    private String favPoster;

    private char isPublic;

}
