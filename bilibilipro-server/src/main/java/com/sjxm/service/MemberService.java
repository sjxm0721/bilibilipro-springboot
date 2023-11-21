package com.sjxm.service;

import com.sjxm.result.PageResult;

public interface MemberService {

    PageResult videopage(Long uid, Integer order, Integer page, Integer pageSize);
}
