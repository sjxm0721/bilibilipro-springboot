package com.sjxm.controller.user;

import com.sjxm.dto.DynamicDTO;
import com.sjxm.result.PageResult;
import com.sjxm.result.Result;
import com.sjxm.service.DynamicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/dynamic")
@Api(tags = "动态相关接口")
public class DynamicController {

    @Autowired
    private DynamicService dynamicService;

    @GetMapping("/page")
    @ApiOperation("获取动态分页列表数据")
    public Result<PageResult> page(Integer page,Integer pageSize,Long uid){

        PageResult pageResult = dynamicService.page(page,pageSize,uid);

        return Result.success(pageResult);
    }

    @PostMapping("/add")
    @ApiOperation("发表动态")
    public Result add(@RequestBody DynamicDTO dynamicDTO){

        dynamicService.add(dynamicDTO);
        return Result.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除动态")
    public Result delete(Long dynamicId){
        dynamicService.delete(dynamicId);
        return Result.success();
    }


}
