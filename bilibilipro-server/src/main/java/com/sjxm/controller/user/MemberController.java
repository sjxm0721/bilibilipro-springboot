package com.sjxm.controller.user;

import com.sjxm.result.PageResult;
import com.sjxm.result.Result;
import com.sjxm.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/member")
@Api(tags = "bp会员信息接口")
@Slf4j
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 根据uid获取用户投稿视频的分页信息
     * @param uid
     * @param order
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/videopage")
    @ApiOperation("根据用户id获取用户投稿数据")
    public Result<PageResult> videopagebyuid(Long uid, Integer order, Integer page, Integer pageSize){
        PageResult pageResult = memberService.videopage(uid,order,page,pageSize);
        return Result.success(pageResult);
    }
}
