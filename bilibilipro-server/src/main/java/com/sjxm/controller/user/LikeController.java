package com.sjxm.controller.user;

import com.sjxm.dto.LikeDTO;
import com.sjxm.result.PageResult;
import com.sjxm.result.Result;
import com.sjxm.service.LikeService;
import com.sjxm.vo.LikeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@RestController
@RequestMapping("/user/like")
@Slf4j
@Api(tags="用户点赞相关接口")
public class LikeController implements Serializable {

    @Autowired
    private LikeService likeService;

    @GetMapping("/videopage")
    @ApiOperation("获取用户点赞视频分页列表")
    public Result<PageResult> videoPage(Long uid,Integer page,Integer pageSize){
        PageResult pageResult = likeService.videoPage(uid,page,pageSize);
        return Result.success(pageResult);
    }

    @GetMapping("/list")
    @ApiOperation("获取用户点赞数据列表")
    public Result<List<LikeVO>> list(Long uid,char type){
        List<LikeVO> list = likeService.list(uid,type);
        return Result.success(list);
    }


    @PostMapping("/add")
    @ApiOperation("用户点赞")
    public Result add(@RequestBody LikeDTO likeDTO){
        likeService.add(likeDTO);
        return Result.success();
    }


    @PostMapping("/cancel")
    @ApiOperation("用户取消点赞")
    public Result cancel(@RequestBody LikeDTO likeDTO){
        likeService.cancel(likeDTO);
        return Result.success();
    }


}
