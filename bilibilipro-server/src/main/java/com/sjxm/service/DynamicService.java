package com.sjxm.service;

import com.sjxm.dto.DynamicDTO;
import com.sjxm.result.PageResult;
import com.sjxm.vo.DynamicVO;

public interface DynamicService {
    PageResult page(Integer page,Integer pageSize,Long uid);

    void update(DynamicVO dynamicVO);

    DynamicVO info(Long dynamicId);

    void add(DynamicDTO dynamicDTO);

    void delete(Long dynamicId);
}
