package com.epam.esm.aspect;

import com.epam.esm.dto.CertificateTagsDto;
import com.epam.esm.entity.MethodMetadata;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class ServiceLoggingAspect {
    @Pointcut("execution(*..CertificateTagsDto com.epam.esm.service.*..update*(..))")
    public void updateServiceMethodPointcut() {
    }

    @Pointcut("execution(int com.epam.esm.service.*..delete*(int))")
    public void deleteServiceMethodPointcut() {
    }

    @Pointcut("execution(* com.epam.esm.service.*..find*(..))")
    public void findDtoServiceMethodPointcut() {
    }

    @Pointcut("execution(*..*Dto com.epam.esm.service.*..add*(..))")
    public void addServiceMethodPointcut() {
    }

    @Pointcut("execution(* com.epam.esm.service.impl.*.*(..))")
    public void allServiceMethodPointcut() {
    }

    @AfterReturning(pointcut = "updateServiceMethodPointcut()", returning = "result")
    public void logUpdateDeleteServiceMethod(JoinPoint joinPoint, CertificateTagsDto result) {
        MethodMetadata methodMetadata = takeMethodMetadata(joinPoint);

        log.info("Method {}() from class {} worked successfully. {} affected row",
                methodMetadata.methodName(), methodMetadata.className(), result);
    }

    @AfterReturning(pointcut = "deleteServiceMethodPointcut()", returning = "affectedRow")
    public void logDeleteServiceMethod(JoinPoint joinPoint, int affectedRow) {
        MethodMetadata methodMetadata = takeMethodMetadata(joinPoint);

        log.info("Method {}() from class {} worked successfully. {} affected row",
                methodMetadata.methodName(), methodMetadata.className(), affectedRow);
    }

    @AfterReturning(pointcut = "findDtoServiceMethodPointcut()", returning = "result")
    public void logFindDtoServiceMethod(JoinPoint joinPoint, Object result) {
        MethodMetadata methodMetadata = takeMethodMetadata(joinPoint);

        log.info("Method {}() from class {} returned {}",
                methodMetadata.methodName(), methodMetadata.className(), result);
    }

    @AfterReturning(pointcut = "addServiceMethodPointcut()", returning = "result")
    public void logAddServiceMethod(JoinPoint joinPoint, Object result) {
        MethodMetadata methodMetadata = takeMethodMetadata(joinPoint);

        log.info("Method {}() from class {} returned id {}",
                methodMetadata.methodName(), methodMetadata.className(), result);
    }

    @AfterThrowing(pointcut = "allServiceMethodPointcut()", throwing = "exception")
    public void logExceptionAllServiceMethod(JoinPoint joinPoint, Exception exception) {
        MethodMetadata methodMetadata = takeMethodMetadata(joinPoint);

        log.error("Method {}() from class {} thrown exception {}: {}",
                methodMetadata.methodName(), methodMetadata.className(), exception.getClass(), exception.getMessage());
    }

    private MethodMetadata takeMethodMetadata(JoinPoint joinPoint) {
        Signature methodSignature = joinPoint.getSignature();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        return new MethodMetadata(className, methodName);
    }
}
