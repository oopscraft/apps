package org.oopscraft.apps.core.data;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RoutingDataSourceAspect {

    @Around("@annotation(routingDataSourceKey)")
    public Object determineCurrentLookupKey(ProceedingJoinPoint joinPoint, RoutingDataSourceKey routingDataSourceKey) throws Throwable {
        log.info("RoutingDataSourceKey.value:{}", routingDataSourceKey.value());
        try {
            RoutingDataSource.setKey(routingDataSourceKey.value());
            Object proceed = joinPoint.proceed();
            return proceed;
        }finally{
            RoutingDataSource.clearKey();
        }
    }
}
