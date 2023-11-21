package com.sjxm.service;

import com.sjxm.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    List<CategoryVO> homecategory();

    CategoryVO info(Long categoryId);

    void update(CategoryVO categoryVO);

    void transCategoryFromRedis2DB();
}
