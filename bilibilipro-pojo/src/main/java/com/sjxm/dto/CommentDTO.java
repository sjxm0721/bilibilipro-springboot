package com.sjxm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO implements Serializable {

    private Long uid;

    private Long videoId;

    private Long dynamicId;

    private Long fatherId;

    private Long totalFatherId;

    private String content;

}
