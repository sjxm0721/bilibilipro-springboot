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
public class Search implements Serializable {

    private Long searchId;

    private String searchContent;

    private Integer searchNum;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
