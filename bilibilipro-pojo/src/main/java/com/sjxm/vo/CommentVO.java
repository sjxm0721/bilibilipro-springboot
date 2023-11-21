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
public class CommentVO implements Serializable {

    private Long commentId;

    private Long uid;

    private Long videoId;

    private Long dynamicId;

    private Long fatherId;

    private Long totalFatherId;

    private String content;

    private LocalDateTime postTime;

    private Integer likeNum;

    private String avatar;

    private String accountName;

    private List<CommentVO> subComment;

    private String responseName;

}
