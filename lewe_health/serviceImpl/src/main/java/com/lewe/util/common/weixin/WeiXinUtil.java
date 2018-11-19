package com.lewe.util.common.weixin;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lewe.util.common.HttpUtil;
import com.lewe.util.common.PropertiesUtil;

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
    @SuppressWarnings("deprecation")
	public static String concatUrlForGetCode(String scope){
    	//String redirectUri = "https://aijutong.com/lewe/customer/authCallback.do";
    	String redirectUri = "http://2w26a28982.imwork.net/lewe/customer/authCallback.do";
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

}
