package com.lewe.util.common.weixin;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.UUID;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.alibaba.fastjson.JSONObject;
import com.lewe.util.common.HttpUtil;
import com.lewe.util.common.JedisUtil;
import com.lewe.util.common.PropertiesUtil;
import com.lewe.util.common.StringUtils;

@SuppressWarnings("deprecation")
public class WeiXinUtil {
	public static final Logger logger = LoggerFactory.getLogger(WeiXinUtil.class); 
	private static final String appId = PropertiesUtil.getApiPropertyByKey("gongzhonghao.appID");
	private static final String appSecret = PropertiesUtil.getApiPropertyByKey("gongzhonghao.appSecret");

	/**
     * 生成用于获取code的URL地址给前端
     * scope : snsapi_base 静默授权,只能得到openId
     * scope : snsapi_userinfo 非静默授权,弹出授权页面,可通过openId拿到昵称、性别、所在地等
     * @return 返回前端请求微信服务器返回的code码的URL地址
     */
	public static String concatUrlForGetCode(String scope,int pageType){
    	String redirectUri = "https://aijutong.com/lewe/customer/authCallback.do?pageType="+pageType;
    	//String redirectUri = "http://2w26a28982.imwork.net/lewe/customer/authCallback.do?pageType="+pageType;
    	redirectUri = URLEncoder.encode(redirectUri);
    	String apiUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";
    	//拼接授权验证地址获取code码的URL
    	String authCheckUrl = String.format(apiUrl,appId,redirectUri,scope,"xxxx_state");
        return authCheckUrl;
    }

	public static JSONObject getUserInfoAccessToken(String code) {
		JSONObject json = null;
	    try {
	    	String apiUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
	        String url = String.format(apiUrl,appId, appSecret, code);
	        logger.info("请求userInfoAccessToken，参数URL={}",url);
	        String tokens = HttpUtil.doGet(url);
	        json = JSONObject.parseObject(tokens, JSONObject.class);
	        logger.info("请求userInfoAccessToken成功.[result={}]", json);
	    } catch (Exception e) {
	        logger.error("请求userInfoAccessToken失败.[error={}]", e);
	    }
	    return json;
	}

	public static JSONObject getUserInfo(String accessToken, String openId) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
        logger.info("请求微信用户信息，参数URL={}",url);
        JSONObject userInfo = null;
        try {
            String jsonStr = HttpUtil.doGet(url);
            userInfo = JSONObject.parseObject(jsonStr, JSONObject.class);
            logger.info("请求微信用户信息成功.[result={}]", userInfo);
        } catch (Exception e) {
            logger.error("请求微信用户信息失败.[error={}]", e);
        }
        return userInfo;
    }

	/**
     * 生成JS调用微信接口的签名参数
     * @param url当前页面的URL
     * @return
     * @throws Exception
     */
    public static JSONObject createSignature(String url) throws Exception{
    	String signature = "jsapi_ticket="+getJsapiTicket();
    	String noncestr = createNonceStr();
    	long timestamp = System.currentTimeMillis()/1000;
    	signature = signature+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;
    	//根据jsapi_ticket等参数进行SHA1加密
    	signature = SHA1(signature);
    	JSONObject json = new JSONObject();
    	json.put("appid", appId);
    	json.put("noncestr", noncestr);
    	json.put("signature", signature.toLowerCase());
    	json.put("timestamp", timestamp);
    	return json;
    }

    /**
     * 公众号获取全局的access_token
     * @return
     * @throws Exception
     */
    public static String getBaseAccessToken() throws Exception{ 
    	JedisUtil jedis = JedisUtil.getInstance();
    	String baseAccessToken = jedis.get("LEWE_WEIXIN_BASE_ACCESS_TOKEN");
    	if(StringUtils.isNotBlank(baseAccessToken)){
    		return baseAccessToken;
    	}else{
    		//缓存中没有,或已经失效
		    String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+ appSecret;	    
		    String res = sendHttpGetRequest(url);
		    logger.info("向微信获取access_token,返回={}",res);
		    JSONObject jsonObj = JSONObject.parseObject(res);
		    baseAccessToken = jsonObj.getString("access_token");
		    Integer expiresTime = Integer.parseInt(jsonObj.getString("expires_in"));
		    //将baseAccessToken缓存
		    jedis.set("LEWE_WEIXIN_BASE_ACCESS_TOKEN", baseAccessToken, expiresTime-1800);
    	}
    	  logger.info("程序获取获取access_token,返回={}",baseAccessToken);
    	return baseAccessToken;
    }
    @SuppressWarnings("resource")
    public static String sendHttpGetRequest(String url) throws Exception{
    	HttpGet httpGet = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient(); 
        HttpResponse response = httpclient.execute(httpGet);
        HttpEntity httpEntity = response.getEntity();
        String result = EntityUtils.toString(httpEntity, "utf-8");
        return result;
    }
    /**
     * 公众号获取全局的jsapi_ticket
     * @return
     * @throws Exception
     */
    public static String getJsapiTicket() throws Exception{
    	JedisUtil jedis = JedisUtil.getInstance();
    	String jsapiTicket = jedis.get("LEWE_WEIXIN_JS_API_TICKET");
    	if(StringUtils.isNotBlank(jsapiTicket)){
    		return jsapiTicket;
    	}else{
    		//缓存中没有,或已经失效
	    	String baseAccessToken = getBaseAccessToken();
	    	String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+ baseAccessToken +"&type=jsapi";
	    	String res = sendHttpGetRequest(url);
	    	JSONObject jsonObj = JSONObject.parseObject(res);
	    	jsapiTicket = jsonObj.getString("ticket");
	    	Integer expiresTime = Integer.parseInt(jsonObj.getString("expires_in"));
	    	//将jsapiTicket缓存
    		jedis.set("LEWE_WEIXIN_JS_API_TICKET", jsapiTicket, expiresTime-1800);
    	}
    	return jsapiTicket;
    }
    /**
     * 生成随机字符串
     * @return
     */
    private static String createNonceStr() {
    	return UUID.randomUUID().toString().replaceAll("-", "");
    }
    /**
     * SHA1 加密算法
     * @param str
     * @return
     */
    public static String SHA1(String str) {
        try {
        	//如果是SHA加密只需要将"SHA-1"改成"SHA"即可
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1"); 
            digest.reset();
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexStr = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexStr.append(0);
                }
                hexStr.append(shaHex);
            }
            return hexStr.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
