package com.sjxm.controller.user;

import com.sjxm.dto.SearchDTO;
import com.sjxm.result.PageResult;
import com.sjxm.result.Result;
import com.sjxm.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags="搜索相关接口")
@RestController
@RequestMapping("/user/search")
@Slf4j
public class SearchController {

    @Autowired
    private SearchService searchService;

    @ApiOperation("获取热搜列表数据")
    @GetMapping("/list")
    public Result<PageResult> list(Integer page,Integer pageSize){

        PageResult pageResult = searchService.list(page,pageSize);

        return Result.success(pageResult);
    }

    @ApiOperation("增加搜索数据")
    @PutMapping("/add")
    public Result add(@RequestBody SearchDTO searchDTO){

        searchService.add(searchDTO.getSearchContent());
        return Result.success();
    }

}
