package com.ticket.ticket.logging;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BenchmarkAspect {
    private static final Logger logger = LoggerFactory.getLogger(BenchmarkAspect.class);

    @Around("execution(* com.ticket..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        logger.info("{} executed in {} ms", joinPoint.getSignature().getName(), end - start);
        return result;
    }
}
