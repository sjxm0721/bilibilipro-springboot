package com.sjxm.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sjxm.dto.HistoryDTO;
import com.sjxm.result.PageResult;
import com.sjxm.result.Result;
import com.sjxm.service.HistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/history")
@Api(tags = "浏览记录相关接口")
@Slf4j
public class HistoryController {

    @Autowired
    private HistoryService historyService;


    @ApiOperation("获取用户浏览记录分页数据")
    @GetMapping("/page")
    public Result<PageResult> page(Long uid, Integer page, Integer pageSize) throws JsonProcessingException {

        PageResult pageResult = historyService.page(uid,page,pageSize);

        return Result.success(pageResult);
    }

    @ApiOperation("添加用户浏览记录")
    @PostMapping("/add")
    public Result add(@RequestBody HistoryDTO historyDTO) throws JsonProcessingException {
        historyService.add(historyDTO);

        return Result.success();
    }

}
