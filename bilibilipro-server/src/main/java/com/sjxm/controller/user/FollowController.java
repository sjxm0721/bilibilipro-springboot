package com.sjxm.controller.user;

import com.sjxm.dto.FollowDTO;
import com.sjxm.result.PageResult;
import com.sjxm.result.Result;
import com.sjxm.service.FollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/follow")
@Api(tags = "关注相关接口数据")
public class FollowController {

    @Autowired
    private FollowService followService;


    @GetMapping("/getfollow")
    @ApiOperation("获取关注列表分页数据")
    public Result<PageResult> follow(Integer page,Integer pageSize,Long uid){

        PageResult pageResult = followService.follow(page,pageSize,uid);

        return Result.success(pageResult);
    }

    @GetMapping("/getfans")
    @ApiOperation("获取粉丝列表分页数据")
    public Result<PageResult> fans(Integer page,Integer pageSize,Long uid){

        PageResult pageResult = followService.fans(page,pageSize,uid);

        return Result.success(pageResult);
    }

    @PostMapping("/add")
    @ApiOperation("点击关注")
    public Result add(@RequestBody FollowDTO followDTO){

        followService.add(followDTO);

        return Result.success();
    }


    @PostMapping("/cancel")
    @ApiOperation("取消关注")
    public Result cancel(@RequestBody FollowDTO followDTO){

        followService.cancel(followDTO);

        return Result.success();
    }


    @GetMapping("/isfollow")
    @ApiOperation("查询是否关注")
    public Result<Boolean> isfollow(Long followerUid,Long followedUid){

         Boolean flag = followService.isfollow(followerUid,followedUid);

         return Result.success(flag);
    }
}
