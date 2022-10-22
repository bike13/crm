package com.xxxx.crm.interceptor;

import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//免登录
public class NoLoginInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //取id
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);

        if (userId==0||userService.selectByPrimaryKey(userId)==null){
            //抛异常  存在默认值
            throw new NoLoginException();
        }
        return super.preHandle(request, response, handler);
    }
}
