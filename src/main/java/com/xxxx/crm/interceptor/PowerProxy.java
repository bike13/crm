package com.xxxx.crm.interceptor;

import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.exceptions.PowerException;
import com.xxxx.crm.utils.Power;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.util.List;


//权限的切面
@Controller
@Aspect
public class PowerProxy {
    @Resource
    private HttpSession session;
    //环绕通知 切入方法(接口)
    @Around(value = "@annotation(com.xxxx.crm.utils.Power)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object result=null;
        //前置通知
        //将存储的数据取出
        List<String> Permissionlist= (List<String>) session.getAttribute("Permissionlist");
        //判断Permissionlist中是否包含注解中的值
        if (Permissionlist==null && Permissionlist.size()<1){
            throw new PowerException();
        }
        //得到目标对象
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        //得到注解值
        Power powercode=methodSignature.getMethod().getDeclaredAnnotation(Power.class);
        //@com.xxxx.crm.utils.Power(code=1010)??????没有获得到对应的
        if (!Permissionlist.contains(powercode.code())){
            throw new PowerException();
        }
        pjp.proceed();

        result=pjp.proceed();
        return result;
    }
}
