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
public class Comment implements Serializable {

    private Long commentId;

    private Long uid;

    private Long videoId;

    private Long dynamicId;

    private Long fatherId;

    private String content;

    private LocalDateTime postTime;

    private Integer likeNum;


    private Long totalFatherId;

}
