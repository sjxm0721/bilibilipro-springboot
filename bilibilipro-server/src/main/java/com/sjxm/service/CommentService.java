package com.sjxm.service;

import com.sjxm.dto.CommentDTO;
import com.sjxm.result.PageResult;
import com.sjxm.vo.CommentVO;

public interface CommentService {
    PageResult page(Integer page, Integer pageSize, Integer order, Integer dynamicId, Integer videoId);

    Long reply(CommentDTO commentDTO);

    CommentVO info(Long commentId);

    void update(CommentVO commentVO);

    void transCommentFromRedis2DB();

    void deleteByDynamicId(Long dynamicId);

    void delete(Long commentId);
}
