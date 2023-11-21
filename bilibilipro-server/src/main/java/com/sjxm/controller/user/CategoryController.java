package com.sjxm.controller.user;

import com.sjxm.result.Result;
import com.sjxm.service.CategoryService;
import com.sjxm.vo.CategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/category")
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取首页分类列表的数据
     * @return
     */
    @GetMapping("/homecategory")
    @ApiOperation("获取首页分类列表数据")
    public Result<List<CategoryVO>> homecategory(){

        List<CategoryVO> list = categoryService.homecategory();

        return Result.success(list);
    }

}
