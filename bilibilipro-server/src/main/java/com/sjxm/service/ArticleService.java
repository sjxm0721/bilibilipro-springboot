package com.sjxm.service;

import com.sjxm.dto.ArticleDTO;
import com.sjxm.dto.ArticleQueryDTO;
import com.sjxm.vo.ArticleVO;

import java.util.List;

public interface ArticleService {
    Long add(ArticleDTO articleDTO);

    List<ArticleVO> list(ArticleQueryDTO articleQueryDTO);

    ArticleVO get(Long articleId);
}
