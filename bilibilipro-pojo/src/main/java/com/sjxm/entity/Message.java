package com.sjxm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message implements Serializable {

    private Long messageId;

    private Long fromUid;

    private Long toUid;

    private String content;

    private LocalDateTime postTime;

    private char isRead;

    private char type;

    private Long videoId;

    private Long dynamicId;

    private Long commentId;

    private Long barrageId;

    private char isSystem;

    private Long nowCommentId;

}
