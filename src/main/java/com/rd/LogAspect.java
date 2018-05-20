package com.rd;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.rd.domain.AuditLog;
import com.rd.service.AuditLogService;
import com.rd.utils.CommonUtils;

@Aspect
public class LogAspect {
	
	@Autowired
	AuditLogService service;

//	static Logger logger = LoggerFactory.getLogger("com.rd.LogAspect");
	
	@Pointcut("execution(* com.rd.controller.*.*(..))")  
    public void allServicesFunctions(){}  
      
		
    @Around("allServicesFunctions()")  
    public Object responseTime(ProceedingJoinPoint pjp) throws Throwable   
    {  
    	long start = System.currentTimeMillis();
        Object output = pjp.proceed();
        Long elapsedTime = System.currentTimeMillis() - start;
        String className =pjp.getTarget().getClass().getName();
        String method=pjp.getSignature().getName();
        service.persist(new AuditLog(className+" Action : "+method,new Date(), elapsedTime, CommonUtils.getPrincipal()));
        return output; 
    }  
    
   
	
	
}
