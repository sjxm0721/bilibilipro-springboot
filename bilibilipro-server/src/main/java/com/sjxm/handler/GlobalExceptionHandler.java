package com.sjxm.handler;

import com.sjxm.exception.AccountNotFoundException;
import com.sjxm.exception.BaseException;
import com.sjxm.exception.PasswordErrorException;
import com.sjxm.exception.VideoNotFoundException;
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

    @ExceptionHandler({VideoNotFoundException.class})
    public Result videoUploadExceptionHandler(Exception ex){
        log.error("视频上传异常：{}",ex.getMessage());
        return Result.error(ex.getMessage(),504);
    }


}
