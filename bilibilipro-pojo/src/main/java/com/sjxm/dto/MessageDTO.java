package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageDTO implements Serializable {

    private Long fromUid;

    private Long toUid;

    private String content;

    private char type;

    private Long videoId;

    private Long dynamicId;

    private Long commentId;

    private Long barrageId;

    private boolean isAll;

    private char isSystem;

    private Long nowCommentId;



}
