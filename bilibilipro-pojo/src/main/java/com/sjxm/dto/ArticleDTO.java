package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ArticleDTO implements Serializable {

    private String title;
    private String content;
    private String poster;
    private List<Long> tags;
    private Integer category;
    private String brief;

}
