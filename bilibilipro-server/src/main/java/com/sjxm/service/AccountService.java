package com.sjxm.service;

import com.sjxm.dto.AccountDTO;
import com.sjxm.vo.AccountVO;

public interface AccountService {


    String login(AccountDTO accountDTO);

    AccountVO myInfo(String token);

    void update(AccountVO accountVO);

    AccountVO info(Long uid);

    void click(Long uid);

    void transAccountFromRedis2DB();
}
