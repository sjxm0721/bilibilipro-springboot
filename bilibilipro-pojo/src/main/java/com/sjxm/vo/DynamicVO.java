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
public class DynamicVO implements Serializable {

    private Long dynamicId;

    private Long uid;

    private LocalDateTime postTime;

    private String text;

    private Long videoId;

    private Integer likeNum;

    private Integer commentNum;

    private String avatar;

    private String accountName;

    private String title;

    private Integer lastTime;

    private Integer clickNum;

    private Integer barrageNum;

    private String videoBrief;

    private String poster;

}
