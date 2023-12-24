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
public class Article implements Serializable {
    private Long articleId;
    private Long uid;
    private Integer clickNum;
    private Integer likeNum;
    private Integer commentNum;
    private LocalDateTime postTime;
    private String title;
    private String content;
    private String poster;
    private String tags;
    private Integer category;
    private String brief;
}
