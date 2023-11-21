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
public class HistoryVO implements Serializable {

    private Long historyId;

    private Long uid;

    private Long videoId;

    private LocalDateTime createTime;

    private String videoTitle;

    private String videoPoster;

    private Integer lastTime;

    private String accountName;
}
