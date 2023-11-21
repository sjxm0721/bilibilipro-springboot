package com.sjxm.entity;

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
public class Video implements Serializable {

    private Long videoId;

    private String title;

    private String src;

    private LocalDateTime postTime;

    private Integer barrageNum;

    private Integer lastTime;

    private Integer clickNum;

    private  Long uid;

    private String videoBrief;

    private Integer likeNum;

    private Integer coinNum;

    private Integer favNum;

    private String tags;

    private Integer commentNum;

    private String poster;

    private char inSuggest;

    private char inCarousel;

}
