package com.lewe.util.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;


public class SMSUtil {

	public static final Logger logger = LoggerFactory.getLogger(SMSUtil.class); 
	/**
	 * 发送短信功能
	 * 正确返回 OK
	 * 错误返回错误的原因
	 * @param phone   手机号码
	 * @param message 短信内容
	 * @param msgSign 短信签名
	 * @return
	 */
	public static String sendSMS(String phone, String message,String msgSign){
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("phone", phone);
		params.put("message", message);
		params.put("msgSign", msgSign);
		//短信类型(目前调用的公司内部的短信接口 tz表示通知类短信)
		params.put("type", "tz");
		
		ApiResult result = getResultOfSMS(params);
		if(result!=null){
			if(result.getCode()==0){
				return "OK";
			}else{
				//短信接口返回的错误信息在data字段中
				return result.getData().toString();
			}
		}else{
			return "error";
		}
	}
	
	/**
     * 调用短信平台接口
     * @return
     */
    public static ApiResult getResultOfSMS(Map<String, Object> requestParams) {
    	ApiResult apiResult = new ApiResult();
        StringBuilder data = new StringBuilder();
        // 获取接口URL
        String url = PropertiesUtil.getApiPropertyByKey("sms.url");
        // 获取appkey
        String appkey = PropertiesUtil.getApiPropertyByKey("sms.appkey");
        // 获取密钥
        String keysecret = PropertiesUtil.getApiPropertyByKey("sms.keysecret");
        // 获取msg_sign
        String msgSign = PropertiesUtil.getApiPropertyByKey("sms.sign");
        // 获取当前请求时间(注意不是时间戳)
        String currentDateTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        //短信签名
        msgSign = requestParams.get("msgSign")==null?msgSign:requestParams.get("msgSign").toString();
        
        // 生成sign
        Map<String, String> signParams = new HashMap<String, String>();
        signParams.put("appkey", appkey);
        signParams.put("timestamp", currentDateTime);
        //新的接口把所有参数作为签名规则进行加密(新接口的签名规则要求业务参数也进行签名加密)
        signParams.put("phone", requestParams.get("phone").toString());
        signParams.put("message", requestParams.get("message").toString());
        signParams.put("type", requestParams.get("type").toString());
        signParams.put("msg_sign", msgSign);
        
        String sign = getSignature(signParams, keysecret);
        // 组装请求参数
        data = data.append("appkey=").append(appkey).append("&").append("timestamp=").append(currentDateTime).append("&")
                .append("sign=").append(sign).append("&").append("phone=").append(requestParams.get("phone").toString())
                .append("&").append("message=").append(requestParams.get("message").toString()).append("&").append("msg_sign=")
                .append(msgSign).append("&").append("type=").append(requestParams.get("type").toString());
        
        logger.info("调用发送短信接口请求参数:{}",data.toString());
        String resultStr = HttpUtil.doPost(url, data.toString());
        logger.info("调用发送短信接口返回结果:{}",resultStr);
        JSONObject resultJson = JSONObject.parseObject(resultStr);
        apiResult.setCode(resultJson.getIntValue("code"));
        apiResult.setMessage(resultJson.getString("data"));
        
        return apiResult;
    }
    /**
     * 短信接口请求签名
     * @param params
     * @param secret
     * @return
     */
    public static String getSignature(Map<String, String> params, String secret) {
        // 先将参数以其参数名的字典序升序进行排序
        Map<String, String> sortedParams = new TreeMap<String, String>(params);
        Set<Entry<String, String>> entrys = sortedParams.entrySet();

        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder basestring = new StringBuilder();
        for (Entry<String, String> param : entrys) {
            basestring.append(param.getKey()).append(param.getValue());
        }
        basestring.append("keysecret").append(secret);
        String sign = MD5.md5(basestring.toString());
        return sign.toString();
    }
    
}
