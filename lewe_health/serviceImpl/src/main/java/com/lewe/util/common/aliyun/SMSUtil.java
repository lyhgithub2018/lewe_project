package com.lewe.util.common.aliyun;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class SMSUtil {

    // 产品名称:云通信短信API产品,开发者无需替换
    private static final String product = "Dysmsapi";
    // 产品域名,开发者无需替换
    private static final String domain = "dysmsapi.aliyuncs.com";

    // 阿里云短信api访问密钥及模板code
    private static String accessKeyId = "LTAIC3HuvCtiDzRu";
    private static String accessKeySecret = "3Bcq5nnIUzWTzmlCKcsBuEIfJKEter";
    private static String signName = "乐观健康";
    private static String registTempleteCode = "SMS_151232210";//注册短信模板code
    private static String loginTempleteCode = "SMS_151177310";//登录短信模板code
    private static String reportNoticeTempleteCode = "SMS_151177378";//报告通知模板短信code
    
    public static final Logger logger = LoggerFactory.getLogger(SMSUtil.class); 

    /**
     * 发送注册短信
     * @param phone 手机号
     * @param code  验证码
     */
    public static String sendRegistSMS(String phone,String code) {
    	String result = "";
    	try {
    		JSONObject json = new JSONObject();
        	json.put("code", code);
 			SendSmsResponse response = sendSms(phone, json.toJSONString(),registTempleteCode);
 			result = response.getCode();
 			logger.info("=============[regist]调用阿里云短信接口返回结果:code={},message={}",response.getCode(),response.getMessage());
 		} catch (ClientException e) {
 			e.printStackTrace();
 			logger.error("调用阿里云短信接口发生异常,{}",e.getMessage());
 		}
    	return result;
    }
    /**
     * 发送登录短信
     * @param phone 手机号
     * @param code  验证码
     */
    public static String sendLoginSMS(String phone,String code) {
    	String result = "";
    	try {
    		JSONObject json = new JSONObject();
        	json.put("code", code);
 			SendSmsResponse response = sendSms(phone, json.toJSONString(),loginTempleteCode);
 			result = response.getCode();
 			logger.info("=============[login]调用阿里云短信接口返回结果:code={},message={}",response.getCode(),response.getMessage());
 		} catch (ClientException e) {
 			e.printStackTrace();
 			logger.error("调用阿里云短信接口发生异常,{}",e.getMessage());
 		}
    	return result;
    }
    /**
     * 发送报告通知短信
     * @param name 姓名
     */
    public static String sendReportNoticeSMS(String phone,String name) {
    	String result = "";
    	try {
    		JSONObject json = new JSONObject();
        	json.put("name", name);
 			SendSmsResponse response = sendSms(phone, json.toJSONString(),reportNoticeTempleteCode);
 			result = response.getCode();
 			logger.info("=============[reportNotice]调用阿里云短信接口返回结果:code={},message={}",response.getCode(),response.getMessage());
 		} catch (ClientException e) {
 			e.printStackTrace();
 			logger.error("调用阿里云短信接口发生异常,{}",e.getMessage());
 		}
    	return result;
    }
    
    public static SendSmsResponse sendSms(String mobile, String templateParam, String templateCode)
            throws ClientException {
        // 可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        // 初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();

        // 必填:手机号
        request.setPhoneNumbers(mobile);
        // 必填:短信签名
        request.setSignName(signName);
        // 必填:短信模板ID(SMS_0000)
        request.setTemplateCode(templateCode);
        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}",此处的值为
        request.setTemplateParam(templateParam);

        // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        // request.setSmsUpExtendCode("90997");
        // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");
        // 此处可能会抛出异常，注意catch
        
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        return sendSmsResponse;
    }
    
    public static void main(String[] args) {
        try {
			//sendLoginSMS("18530998725", "508661");
			sendReportNoticeSMS("18530998725", "小辉");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
