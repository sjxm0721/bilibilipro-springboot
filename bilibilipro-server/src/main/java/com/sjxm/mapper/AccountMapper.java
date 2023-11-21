package com.sjxm.mapper;

import com.sjxm.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper {

    /**
     * 根据accountId获取用户信息
     * @param accountId
     * @return
     */
    @Select("select * from account where account_id = #{accountId}")
    Account getByAccountId(String accountId);

    /**
     * 根据uid获取用户信息
     * @param uid
     * @return
     */
    @Select("select * from account where uid = #{uid}")
    Account getByUid(Long uid);

    void updateAccount(Account account);
}
