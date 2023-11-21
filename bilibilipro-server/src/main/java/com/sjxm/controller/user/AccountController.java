package com.sjxm.controller.user;

import com.sjxm.dto.AccountDTO;
import com.sjxm.result.Result;
import com.sjxm.service.AccountService;
import com.sjxm.vo.AccountVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/account")
@Api(tags = "用户账号相关接口")
public class AccountController {


    @Autowired
    private AccountService accountService;

    /**
     * 用户登陆接口
     * @param accountDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登陆")
    public Result<String> login(@RequestBody AccountDTO accountDTO){

        String token = accountService.login(accountDTO);

        return Result.success(token);
    }

    /**
     * 根据token获取用户数据接口
     * @param token
     * @return
     */
    @GetMapping("/myinfo")
    @ApiOperation("根据token获取用户信息")
    public Result<AccountVO> myinfo(String token){

        AccountVO accountVO = accountService.myInfo(token);

        return Result.success(accountVO);
    }

    @GetMapping("/info")
    @ApiOperation("根据uid获取用户信息数据")
    public Result<AccountVO> info(Long uid){
        AccountVO accountVO = accountService.info(uid);

        return Result.success(accountVO);
    }
}
