package com.sjxm.controller.user;

import com.sjxm.dto.CommentDTO;
import com.sjxm.result.PageResult;
import com.sjxm.result.Result;
import com.sjxm.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/comment")
@Api(tags = "评论相关接口")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("获取评论分页列表数据")
    @GetMapping("/page")
    public Result<PageResult> page(Integer page,Integer pageSize,Integer order,Integer dynamicId,Integer videoId){

        PageResult pageResult = commentService.page(page,pageSize,order,dynamicId,videoId);
        return Result.success(pageResult);
    }


    @ApiOperation("用户发表评论")
    @PostMapping("/reply")
    public Result<Long> reply(@RequestBody CommentDTO commentDTO){

        Long commentId =  commentService.reply(commentDTO);
        return Result.success(commentId);
    }


    @DeleteMapping("/delete")
    @ApiOperation("用户删除评论")
    public Result delete(Long commentId){

        commentService.delete(commentId);
        return Result.success();
    }

}
