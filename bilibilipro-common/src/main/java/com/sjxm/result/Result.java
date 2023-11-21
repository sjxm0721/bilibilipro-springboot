package com.sjxm.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后端通用返回结果类
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：200为成功，其他数字为失败

    private String msg;//错误信息

    private T data;//数据

    public static <T> Result<T> success(){
        Result<T> result = new Result<T>();
        result.code=200;
        return result;
    }

    public static <T> Result<T> success(T object){
        Result<T> result = new Result<T>();
        result.data = object;
        result.code=200;
        return result;
    }

    public static <T> Result<T> error(String msg,Integer code){
        Result<T> result = new Result();
        result.msg = msg;
        result.code=code;
        return result;
    }

}
