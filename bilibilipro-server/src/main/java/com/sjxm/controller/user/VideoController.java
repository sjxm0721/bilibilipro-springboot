package com.sjxm.controller.user;

import com.sjxm.dto.VideoDTO;
import com.sjxm.dto.VideoPageDTO;
import com.sjxm.result.PageResult;
import com.sjxm.result.Result;
import com.sjxm.service.VideoService;
import com.sjxm.vo.VideoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "视频相关接口")
@RestController
@RequestMapping("/user/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 获取视频分类数据
     * @return
     */
    @PostMapping("/page")
    @ApiOperation("获取视频分类数据")
    public Result<PageResult> page(@RequestBody VideoPageDTO videoPageDTO){

        PageResult pageResult = videoService.page(videoPageDTO);

        return Result.success(pageResult);
    }

    /**
     * 获取推荐页视频相关信息
     * @return
     */
    @GetMapping("/homesuggest")
    @ApiOperation("获取推荐位视频信息")
    public Result<List<VideoVO>> homesuggest(){

        List<VideoVO> homesuggest = videoService.homesuggest();

        return Result.success(homesuggest);
    }

    /**
     * 根据id获取视频详细信息
     * @param videoId
     * @return
     */
    @GetMapping("/info")
    @ApiOperation("获取视频详细信息")
    public Result<VideoVO> info(Long videoId){

        VideoVO video= videoService.info(videoId);

        return Result.success(video);
    }

    @GetMapping("/click")
    @ApiOperation("视频点击")
    public Result click(Long videoId){

        videoService.click(videoId);

        return Result.success();

    }

    @GetMapping("/search")
    @ApiOperation("视频搜索")
    public Result<PageResult> search(String searchContent,Integer page,Integer pageSize,Integer order){
        PageResult pageResult = videoService.search(searchContent,page,pageSize,order);
        return Result.success(pageResult);
    }


    @PostMapping("/add")
    @ApiOperation("视频添加")
    public Result<Long> add(@RequestBody VideoDTO videoDTO){

        Long videoId = videoService.add(videoDTO);
        return Result.success(videoId);
    }

}
