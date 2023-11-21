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
public class History implements Serializable {

    private Long historyId;

    private Long uid;

    private Long videoId;

    private LocalDateTime createTime;

}
