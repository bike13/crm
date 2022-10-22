package com.xxxx.crm;

import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.exceptions.PowerException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

//全局异常统一处理
@Component
public class GlobalExceptionResolve implements HandlerExceptionResolver {


    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {

        /**
         * 为登录异常处理
         *
         */
        if (ex instanceof NoLoginException){
            ModelAndView mv= new ModelAndView();
            mv.setViewName("redirect:/index");
            return mv;
        }

        /**
         * 默认异常处理
         *   方法返回值类型
         *       视图
         *       json
         *  如何判断方法返回的是视图  还是 json?
         *     约定:如果方法级别配置@ResponseBody  方法响应内容为json  反之 方法响应内容为html页面
         */
        ModelAndView mv=new ModelAndView();
        mv.setViewName("error");
        mv.addObject("code",500);
        mv.addObject("msg","系统异常，请稍后再试...");

        //如果异常是方法
        if (handler instanceof HandlerMethod){
            HandlerMethod hm= (HandlerMethod) handler;
            //获取方法的注解
            ResponseBody responseBody= hm.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //返回视图
            if (responseBody==null){
                //判读参数为自定义参数异常
                if (ex instanceof ParamsException){
                    ParamsException pe= (ParamsException) ex;
                    mv.addObject("msg",pe.getMsg());
                    mv.addObject("code",pe.getCode());
                }else if (ex instanceof PowerException){
                    PowerException pe= (PowerException) ex;
                    mv.addObject("msg",pe.getMsg());
                    mv.addObject("code",pe.getCode());
                }
            }else {
                //返回为json
                ResultInfo resultInfo=new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统错误,请稍后再试...");
                if (ex instanceof ParamsException){
                    ParamsException pe= (ParamsException) ex;
                    resultInfo.setMsg(pe.getMsg());
                    resultInfo.setCode(pe.getCode());
                }else if (ex instanceof PowerException){
                    PowerException pe= (PowerException) ex;
                    resultInfo.setMsg(pe.getMsg());
                    resultInfo.setCode(pe.getCode());
                }
                //设置编码
                response.setContentType("application/json;charset=utf-8");
                response.setCharacterEncoding("utf-8");
                //建流
                PrintWriter pw=null;
                try {
                    pw=response.getWriter();
                    pw.write(JSON.toJSONString(resultInfo));
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(null !=pw){
                        pw.close();
                    }
                }
                return null;
            }
        }
        return mv;
    }
}
