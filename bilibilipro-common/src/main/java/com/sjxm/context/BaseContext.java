package com.sjxm.context;

/**
 * 线程类相关，使用线程临时空间存储一次请求中需要使用的信息
 * 存储发送请求的用户信息
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setUID(Long id){ threadLocal.set(id);}

    public static void getUID() {threadLocal.get();}

    public static void removeUID() {
        threadLocal.remove();
    }

}
