package com.sjxm.vo;

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
public class MessageVO implements Serializable {

    private Long messageId;

    private Long fromUid;

    private String fromName;

    private String fromAvatar;

    private Long toUid;

    private String toName;

    private String toAvatar;

    private String content;

    private LocalDateTime postTime;

    private char isRead;

    private char type;

    private Long videoId;

    private Long dynamicId;

    private Long commentId;

    private Long barrageId;

    private char isSystem;

    private boolean isAll;

    private String videoPoster;

    private String barrageText;

    private String commentContent;

    private String dynamicText;

    private Long nowCommentId;

    public void setIsAll(boolean flag){
        this.isAll = flag;
    }

    public boolean getIsAll(){
        return this.isAll;
    }


}
