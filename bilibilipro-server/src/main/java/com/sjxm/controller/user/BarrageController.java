package com.sjxm.controller.user;

import com.sjxm.dto.BarrageDTO;
import com.sjxm.entity.Barrage;
import com.sjxm.result.Result;
import com.sjxm.service.BarrageService;
import com.sjxm.vo.BarrageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/barrage")
@Slf4j
@Api(tags = "视频弹幕相关接口")
public class BarrageController {

    @Autowired
    private BarrageService barrageService;


    /**
     * 获取视频列表数据
     * @param videoId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("获取视频弹幕列表")
    public Result<List<BarrageVO>> list(Long videoId){
        List<BarrageVO> list = barrageService.list(videoId);

        return Result.success(list);
    }


    /**
     * 用户发送弹幕
     * @param barrageDTO
     * @return
     */
    @PostMapping("/send")
    @ApiOperation("用户发送弹幕")
    public Result send(@RequestBody BarrageDTO barrageDTO){

        barrageService.send(barrageDTO);

        return Result.success();
    }

}
