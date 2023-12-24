package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ArticleQueryDTO implements Serializable {

    private Integer category;

    private Long uid;

    private Integer chosedTime;

    private String title;

    private Integer chosedOrder;

}
