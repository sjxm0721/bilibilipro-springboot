package com.sjxm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeVO implements Serializable {

    private Long likeId;

    private Long fromUid;

    private Long toUid;

    private char type;

    private Long videoId;

    private Long dynamicId;

    private Long commentId;

    private Long barrageId;

    private char status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
