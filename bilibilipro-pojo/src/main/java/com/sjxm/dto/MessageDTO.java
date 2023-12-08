package com.sjxm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    private String videoUrl;

    private Integer videoLastTime;


}
