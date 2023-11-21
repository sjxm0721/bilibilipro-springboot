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
public class VideoVO implements Serializable {
    private Long videoId;

    private String title;

    private String poster;

    private Integer clickNum;

    private Integer barrageNum;

    private Integer lastTime;

    private String videoBrief;

    private Long uid;

    private String accountName;

    private LocalDateTime postTime;

    private String src;

    private Integer likeNum;

    private Integer coinNum;

    private Integer favNum;

    private List<CategoryVO> categoryTags;

    private Integer commentNum;

    private char inSuggest;

    private char inCarousel;
}
