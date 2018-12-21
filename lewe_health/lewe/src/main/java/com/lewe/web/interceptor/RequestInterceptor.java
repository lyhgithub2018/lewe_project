package com.lewe.web.interceptor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lewe.util.common.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 * @author 小辉
 * @version V1.0
 * @Description 请求拦截器
 * @Data 2018-11-01
 **/
public class RequestInterceptor implements HandlerInterceptor {
 
    public static final Logger LOGGER = LoggerFactory.getLogger(RequestInterceptor.class); 
 
    private static final ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("ThreadLocal StartTime");
 
    /**
     * 该方法将在请求处理之前进行调用。
     * 多个Interceptor，SpringMVC会根据声明的前后顺序一个接一个的执行，而且所有的Interceptor中的preHandle方法都会在
     * Controller方法调用之前调用。SpringMVC的这种Interceptor链式结构也是可以进行中断的，这种中断方式是令preHandle的返
     * 回值为false，当preHandle的返回值为false的时候整个请求就结束了。
     * @param request
     * @param response
     * @param handler
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        startTimeThreadLocal.set(startTime);//线程绑定变量（该数据只有当前请求的线程可见）
        if (handler instanceof HandlerMethod) {
        	StringBuilder sb = new StringBuilder(1000);
            sb.append("========接收到请求:").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(startTime)).append("========\n");
            HandlerMethod h = (HandlerMethod) handler;
            sb.append("URL       : ").append(request.getRequestURL()).append("\n");
            sb.append("Controller: ").append(h.getBean().getClass().getName()).append("\n");
            sb.append("Method    : ").append(h.getMethod().getName()).append("\n");
            if(!h.getMethod().getName().equals("uploadReport")) {//由于前端上传报告文件传递的数据是base64字符串,内容巨多,因此不打印该参数
            	sb.append("Params    : ").append(getParamString(request.getParameterMap()));
            }
            LOGGER.info(sb.toString());
        }

        //String servletPath = request.getServletPath();
        //if(servletPath.startsWith("/")) return true;
      	//response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
      	// response.setHeader("Access-Control-Allow-Origin", "*");
        // response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        // response.setHeader("Access-Control-Allow-Credentials", "true"); //支持cookie跨域

        String origin = request.getHeader("Origin");
        if(StringUtils.isBlank(origin)){
            origin = "*";
        }
        origin.replaceAll("http://", "");
        origin.replaceAll("https://", "");

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods","GET,POST");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,xm-api-type,App-Key,Access-Token,wechat-id,Cookie,token,Content-Type,Sign");
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setContentType("application/json;charset=UTF-8");
        return true;
    }
 
    /**
     * 在当前请求进行处理之后，也就是Controller方法调用之后执行，但是它会在DispatcherServlet进行视图返回渲染之前被调用，
     * 所以我们可以在这个方法中对Controller 处理之后的ModelAndView 对象进行操作。
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        if(handler instanceof HandlerMethod){
            StringBuilder sb = new StringBuilder(1000);
            sb.append("========请求处理完成,消耗时间:").append(executeTime).append("ms").append("========");
            LOGGER.info(sb.toString());
        }
        // response.setHeader("Access-Control-Allow-Origin", "*");
        // response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        // response.setHeader("Access-Control-Allow-Credentials", "true"); //支持cookie跨域
        // response.setContentType("application/json;charset=UTF-8");


        String origin = request.getHeader("Origin");
        if(StringUtils.isBlank(origin)){
            origin = "*";
        }
        origin.replaceAll("http://", "");
        origin.replaceAll("https://", "");
        
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods","GET,POST");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,xm-api-type,App-Key,Access-Token,wechat-id,Cookie,token,Content-Type,Sign");
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setContentType("text/html;charset=UTF-8");

    }
 
    /**
     * 该方法将在整个请求结束之后，也就是在DispatcherServlet渲染了对应的视图之后执行。这个方法的主要作用是用于进行资源清理工作的。
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    	
    }
    /**
     * 获取request对象中的请求参数
     * @param map
     * @return
     */
    private String getParamString(Map<String, String[]> map) {
        StringBuilder sb = new StringBuilder();
        for(Entry<String,String[]> e:map.entrySet()){
            sb.append(e.getKey()).append("=");
            String[] value = e.getValue();
            if(value != null && value.length == 1){
                sb.append(value[0]).append(",");
            }else{
                sb.append(Arrays.toString(value)).append(",");
            }
        }
        return sb.toString();
    }
 
    /**
     * 将ErrorStack转化为String.
     */
    public static String getStackTraceAsString(Throwable e) {
        if (e == null){
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
