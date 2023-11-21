package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LikeDTO implements Serializable {

    private Long fromUid;

    private Long toUid;

    private char type;

    private Long videoId;

    private Long dynamicId;

    private Long commentId;

    private Long barrageId;

    private char status;


}
