package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/18-17:04
 */
@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut(value = "execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){}

    @Before(value = "pointcut()")
    public void doBefore(JoinPoint joinPoint){
        // 记录日志，格式：[ip] 在[时间] 访问了[类.方法]
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String dateTime = String.format("yyyy-MM-dd:HH-mm-ss",new Date());
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        logger.info(String.format("[%s] 在 [%s] 访问了 [%s.%s]",ip,dateTime,className,methodName));
    }

}
