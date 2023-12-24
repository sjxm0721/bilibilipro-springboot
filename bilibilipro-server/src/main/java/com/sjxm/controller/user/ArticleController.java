package com.sjxm.controller.user;

import com.sjxm.dto.ArticleDTO;
import com.sjxm.dto.ArticleQueryDTO;
import com.sjxm.result.Result;
import com.sjxm.service.ArticleService;
import com.sjxm.vo.ArticleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/article")
@Api(tags = "文章接口")
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/add")
    @ApiOperation("添加文章")
    public Result<Long> add(@RequestBody ArticleDTO articleDTO){

        Long articleId = articleService.add(articleDTO);

        return Result.success(articleId);
    }

    @PostMapping("/list")
    @ApiOperation("查询文章")
    public Result<List<ArticleVO>> list(@RequestBody ArticleQueryDTO articleQueryDTO){

        List<ArticleVO> list = articleService.list(articleQueryDTO);

        return Result.success(list);
    }
    
    @GetMapping("/{articleId}")
    @ApiOperation("获取具体文章")
    public Result<ArticleVO> get(@PathVariable Long articleId){

        ArticleVO articleVO = articleService.get(articleId);

        return Result.success(articleVO);
    }

    

}
