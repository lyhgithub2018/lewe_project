package com.lewe.web.interceptor;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultInterceptor implements MethodInterceptor {

    private final static ObjectMapper jsonMapper = new ObjectMapper();  

    public static final Logger LOGGER = LoggerFactory.getLogger(ResultInterceptor.class); 
    
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
         Object result = invocation.proceed(); 
         String methodName = invocation.getMethod().getName();
         if(methodName.endsWith("List")) {
        	 //由于列表接口返回的数据一般都较多,因此不做日志记录(上线后下面代码注释掉)
        	 //LOGGER.info("接口返回结果:" + jsonMapper.writeValueAsString(result));
         }else {
        	 LOGGER.info("接口返回结果:" + jsonMapper.writeValueAsString(result));
         }
        return result;
    }

}
