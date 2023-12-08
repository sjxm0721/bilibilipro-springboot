package com.sjxm.service.impl;

import com.sjxm.constant.JwtClaimsConstant;
import com.sjxm.constant.MessageConstant;
import com.sjxm.dto.AccountDTO;
import com.sjxm.entity.Account;
import com.sjxm.exception.AccountNotFoundException;
import com.sjxm.exception.PasswordErrorException;
import com.sjxm.json.JacksonObjectMapper;
import com.sjxm.mapper.AccountMapper;
import com.sjxm.properties.JwtProperties;
import com.sjxm.service.AccountService;
import com.sjxm.utils.JwtUtil;
import com.sjxm.utils.RedisUtil;
import com.sjxm.vo.AccountVO;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户登陆接口实现类
     * @param accountDTO
     * @return
     */




    @Override
    public String login(AccountDTO accountDTO) {

        String accountId = accountDTO.getAccountId();
        String password = accountDTO.getPassword();

        Account account = accountMapper.getByAccountId(accountId);

        if(account==null){
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        if(!password.equals(account.getPassword())){
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //登陆成功，生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.UID,account.getUid());
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),//密钥
                jwtProperties.getTtl(),//有效时间
                claims//用户身份信息
        );

        return token;
    }

    /**
     * 根据token获取用户信息数据
     * @param token
     * @return
     */
    @Override
    public AccountVO myInfo(String token) {
        Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(),token);
        Long uid = Long.valueOf(claims.get(JwtClaimsConstant.UID).toString());

        return info(uid);
    }

    @Override
    public void update(AccountVO accountVO) {
        String redisKey = "account:uid="+accountVO.getUid();
        redisUtil.set(redisKey,accountVO);
    }

    /**
     * 根据uid获取用户数据
     * @param uid
     * @return
     */
    @Override
    public AccountVO info(Long uid) {

        String redisKey = "account:uid="+uid;

        Object o = redisUtil.get(redisKey);

        if(o!=null){
            JacksonObjectMapper om = new JacksonObjectMapper();

            return om.convertValue(o, AccountVO.class);
        }
        else{

            Account account = accountMapper.getByUid(uid);

            if(account == null){
                throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
            }

            AccountVO accountVO = new AccountVO();

            BeanUtils.copyProperties(account,accountVO);

            redisUtil.set(redisKey,accountVO);

            return accountVO;
        }

    }

    /**
     * 用户点击更新redis数据
     * @param uid
     */
    @Override
    public void click(Long uid) {
        AccountVO accountVO = info(uid);
        accountVO.setClickNum(accountVO.getClickNum()+1);
        update(accountVO);
    }

    @Override
    @Transactional
    public void transAccountFromRedis2DB() {
        Set<String> keys = redisUtil.keys("account:uid=" + "*");
        JacksonObjectMapper om = new JacksonObjectMapper();
        if (keys != null) {
            for (String key : keys) {
                Object o = redisUtil.get(key);
                AccountVO accountVO = om.convertValue(o, AccountVO.class);
                Account account = new Account();
                BeanUtils.copyProperties(accountVO,account);
                accountMapper.updateAccount(account);
                redisUtil.del(key);
            }
        }
    }


}
