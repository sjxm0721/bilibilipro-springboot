package com.sjxm.interceptor;

import com.sjxm.constant.JwtClaimsConstant;
import com.sjxm.context.BaseContext;
import com.sjxm.properties.JwtProperties;
import com.sjxm.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if(!(handler instanceof HandlerMethod)){
            //拦截到静态资源而不是动态方法，直接放行
            return true;
        }

        //从请求头中获取token
        String token = request.getHeader(jwtProperties.getTokenName());

        //校验令牌
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(),token);
            Long uid = Long.valueOf( claims.get(JwtClaimsConstant.UID).toString());
            BaseContext.setUID(uid);
            //放行
            return true;
        }catch (Exception ex){
            //不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }

    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex){
            //清理用户
        BaseContext.removeUID();
    }
}
