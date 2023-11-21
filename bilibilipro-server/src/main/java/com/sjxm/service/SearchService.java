package com.sjxm.service;

import com.sjxm.result.PageResult;
import com.sjxm.vo.SearchVO;

public interface SearchService {
    void add(String searchContent);

    PageResult list(Integer page,Integer pageSize);

    SearchVO info(Long searchId);

    void update(SearchVO searchVO);

    void transSearchFromRedis2DB();


}
