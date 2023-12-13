package com.gymsystem.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeAspect {
    private static final Logger logger = LoggerFactory.getLogger(TimeAspect.class);

    @Around("execution(* com.gymsystem.controller.UserController.* (..))")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        logger.info("Method: " + joinPoint.getSignature().getName() + " Time: " + (end - start) + "ms");
        return result;
    }
}
