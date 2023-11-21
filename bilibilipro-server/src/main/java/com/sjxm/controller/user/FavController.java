package com.sjxm.controller.user;

import com.sjxm.dto.FavDTO;
import com.sjxm.dto.FavVideoDTO;
import com.sjxm.result.PageResult;
import com.sjxm.result.Result;
import com.sjxm.service.FavService;
import com.sjxm.vo.FavVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/fav")
@Slf4j
@Api(tags = "收藏相关接口")
public class FavController {

    @Autowired
    private FavService favService;

    @ApiOperation("获取收藏夹列表数据")
    @GetMapping("/fatherlist")
    public Result<List<FavVo>> fatherlist(Long uid){
         List<FavVo> list = favService.fatherlist(uid);
         return Result.success(list);
    }

    @GetMapping("/page")
    @ApiOperation("获取收藏视频分页列表数据")
    public Result<PageResult> page(Long uid,Integer page,Integer pageSize,Long fatherDic){
        PageResult pageResult = favService.page(uid,page,pageSize,fatherDic);
        return Result.success(pageResult);
    }

    @PostMapping("/addlist")
    @ApiOperation("新建收藏夹")
    public Result addlist(@RequestBody FavDTO favDTO){

        favService.addlist(favDTO);

        return Result.success();
    }


    @PostMapping("/addVideo")
    @ApiOperation("视频加入收藏夹")
    public Result addVideo2Fav(@RequestBody FavVideoDTO favVideoDTO){
        favService.addVideo2Fav(favVideoDTO);

        return Result.success();
    }


    @DeleteMapping("/deleteVideo")
    @ApiOperation("取消收藏夹视频")
    public Result deleteVideoFromFav(Long uid,Long videoId){

        favService.deleteVideoFromAllFav(uid,videoId);
        return Result.success();
    }

    @GetMapping("/totalFavVideo")
    @ApiOperation("获得收藏视频列表")
    public Result<List<FavVo>> getTotalFavVideo(Long uid){
        List<FavVo> list = favService.getTotalFavVideo(uid);
        return Result.success(list);
    }

    @DeleteMapping("/deleteFatherFav")
    @ApiOperation("删除收藏夹")
    public Result deleteFatherFav(Long favId){
        favService.deleteFatherFav(favId);
        return Result.success();
    }
}
