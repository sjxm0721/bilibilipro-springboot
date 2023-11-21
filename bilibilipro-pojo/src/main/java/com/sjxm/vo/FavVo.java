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
public class FavVo implements Serializable {

    private Long favId;

    private Long uid;

    private String name;

    private char isDic;

    private Long videoId;

    private Long fatherDic;

    private String fatherDicTitle;

    private String favTitle;

    private Integer favNum;

    private char isPublic;

    private String favPoster;

    private LocalDateTime createTime;

    private String videoTitle;

    private Integer lastTime;

    private String videoPoster;

    private String videoUName;

}
