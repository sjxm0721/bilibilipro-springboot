package com.sjxm.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleVO implements Serializable {

    private Long articleId;

    private Long uid;

    private Integer category;

    private Integer clickNum;

    private Integer likeNum;

    private Integer commentNum;

    private LocalDateTime postTime;

    private String title;

    private String content;

    private String poster;

    private List<CategoryVO> tags;

    private AccountVO accountVO;

    private String brief;

}
