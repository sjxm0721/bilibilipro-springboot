package com.sjxm.handler;

import com.sjxm.exception.*;
import com.sjxm.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理基础业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}",ex.getMessage());
        return Result.error(ex.getMessage(),500);
    }

    @ExceptionHandler({AccountNotFoundException.class, PasswordErrorException.class})
    public Result loginErrorExceptionHandler(Exception ex){
        log.error("账号登陆异常：{}",ex.getMessage());
        return Result.error(ex.getMessage(),401);
    }

    @ExceptionHandler({
         CategoryNotFoundException.class,
         CommentNotFoundException.class,
         DynamicNotFoundException.class,
         FavNotFoundException.class,
         MessageNotFoundException.class,
         SearchNotFoundException.class,
         VideoNotFoundException.class
    })
    public Result ResourceErrorExceptionHandler(Exception e){
        log.info("资源异常:{}",e.getMessage());
        return Result.error(e.getMessage(),555);
    }

    @ExceptionHandler({VideoUploadFailedException.class})
    public Result videoUploadExceptionHandler(Exception ex){
        log.error("视频上传异常：{}",ex.getMessage());
        return Result.error(ex.getMessage(),504);
    }

    @ExceptionHandler({PicUploadFailedException.class})
    public Result picUploadExceptionHandler(Exception ex){
        log.error("图片上传异常：{}",ex.getMessage());
        return Result.error(ex.getMessage(),505);
    }

}
