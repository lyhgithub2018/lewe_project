package com.lewe.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.customer.CustomerAccount;
import com.lewe.bean.customer.Fans;
import com.lewe.dao.customer.FansMapper;
import com.lewe.service.customer.ICustomerService;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.StringUtils;
import com.lewe.util.common.weixin.WeiXinUtil;
import com.lewe.web.controller.common.BaseController;
import com.lewe.web.util.SLEmojiFilter;

/**
 * C端客户相关的控制器
 * @author 小辉
 */
@Controller
@RequestMapping("/customer/")
public class CustomerController extends BaseController{
	
	@Autowired
	private FansMapper fansMapper;
	@Autowired
	private ICustomerService customerService;
	
	/**
	 * 获取短信验证码
	 * @param phone 手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getSmsCode")
	public ApiResult getSmsCode(HttpServletRequest request,String phone,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		result.setMessage(customerService.sendSmsCode(phone,result));
		return result;
	}
	/**
	 * 注册登录
	 * @param phone 手机号
	 * @param smsCode 短信验证码
	 * @return
	 */
	@ResponseBody
	@RequestMapping("registOrLogin")
	public ApiResult registOrLogin(HttpServletRequest request,String phone,String smsCode,Long fansId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		CustomerAccount customer = customerService.registOrLogin(phone,smsCode,fansId,result);
		result.setData(customer);
		return result;
	}
	
	/**
	 * 微信公众号网页授权
	 * 第一步:前端调用该接口获取授权code码
	 * @param scope 授权作用域
	 * 		  scope:snsapi_base 静默授权,只能得到openId
     * 		  scope:snsapi_userinfo 非静默授权,需弹出授权页面,可通过openId拿到昵称、性别等
     * 注:该接口返回的是一个URL地址,前端拿到该地址去请求微信从而得到code码
	 */
	@ResponseBody
	@RequestMapping("getCodeUrl")
    public ApiResult getCodeUrl(HttpServletRequest request,String scope,HttpServletResponse response) {
		JSONObject json = new JSONObject();
		ApiResult result = new ApiResult();
		HttpSession session = request.getSession();
		Object obj = session.getAttribute("weixinFansId");
		if(obj!=null) {
			json.put("fansExsitSession", 1);//表示当前微信用户存在session,则无需授权
			json.put("codeUrl", null);
		}else {
			String codeUrl = WeiXinUtil.concatUrlForGetCode(scope);
			json.put("fansExsitSession", 0);//表示当前微信用户不存在session,则需要调起微信公众号网页授权
			json.put("codeUrl", codeUrl);
		}
		result.setData(json);
		return result;
	}
	
	/**
	 * 微信公众号网页授权
	 * 第二步:微信回调我们这个接口,我们进一步调微信接口获取到用户的信息
	 * 注:该接口是给微信回调用的
	 * @param code 用于换取access_token
	 */
	@RequestMapping("/authCallback")
    public String authCallback(HttpServletRequest request,String code,HttpServletResponse response) {
		JSONObject json = WeiXinUtil.getUserInfoAccessToken(code);
		String openId = json.getString("openid");
	    String accessToken = json.getString("access_token");
	    if(StringUtils.isBlank(openId)){
	    	 return "授权失败,未获取到openid";
	    }else {
	    	//通过参数accessToken及openId获取用户信息(包含头像,昵称,性别等)
			JSONObject userInfo = WeiXinUtil.getUserInfo(accessToken, openId);
			Fans fans = new Fans();
			fans.setOpenId(openId);
			fans.setCity(userInfo.getString("city"));
			fans.setCountry(userInfo.getString("country"));
			fans.setProvince(userInfo.getString("province"));
			fans.setHeadImgUrl(userInfo.getString("headimgurl"));
			fans.setCreateTime(new Date());
			String sex = json.getString("sex");
			if("1".equals(sex)){
				fans.setSex((byte)1);
			}else if("2".equals(sex)){
				fans.setSex((byte)2);
			}else{
				//如果未获取到性别,则默认男
				fans.setSex((byte)1);
			}
			String nickName = userInfo.getString("nickname");
			try {
				//过滤昵称中含有的非法字符
				String filterEmoji = SLEmojiFilter.filterEmoji(nickName);
				fans.setNickName(filterEmoji);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Fans fansDB = fansMapper.selectByOpenId(openId);
			if(fansDB==null) {
				fansMapper.insertSelective(fans);
			}else {
				fans.setId(fansDB.getId());
				fansMapper.updateByPrimaryKeySelective(fans);
			}
			//存储session信息
        	HttpSession session = request.getSession();
	        //该值存储到session中是为了让系统中都可以获取到用户的fansId
	        session.setAttribute("weixinFansId", fans.getId());
	        //设置session有效时间为20小时
	        session.setMaxInactiveInterval(20*60*60);
	    }
	    //授权成功后重定向到前端页面
	    //return "redirect:https://aijutong.com/";
	    return "redirect:https://aijutong.com/";
	}
	
	/**
	 * 绑定采样报告编号
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("bindSampleCode")
	public ApiResult bindSampleCode(HttpServletRequest request,HttpServletResponse response) {
		String customerId = request.getParameter("customerId");
		String sampleCode = request.getParameter("sampleCode");
		ApiResult result = new ApiResult();
		customerService.bindSampleCode(customerId,sampleCode,result);
		return result;
	}
	/**
	 * C端客户提交问卷调查
	 * @param questionnaire1 问卷信息1
	 * @param questionnaire2 问卷信息2
	 * @return
	 */
	@ResponseBody
	@RequestMapping("submitQuestionnaire")
	public ApiResult submitQuestionnaire(HttpServletRequest request,String questionnaire1,String questionnaire2,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		customerService.submitQuestionnaire(questionnaire1,questionnaire2,result);
		return result;
	}
	/**
	 * C端用户--我的报告列表
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("myReportList")
	public ApiResult myReportList(HttpServletRequest request,Long customerId,String keyword,String beginDate,String endDate,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		JSONObject json = customerService.myReportList(customerId,keyword,beginDate,endDate,result);
		result.setData(json);
		return result;
	}
	/**
	 * C端用户--我的报告详情
	 * @param reportId 报告信息主键id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("myReportInfo")
	public ApiResult myReportInfo(HttpServletRequest request,Long reportId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		JSONObject json = customerService.myReportInfo(reportId,result);
		result.setData(json);
		return result;
	}
}
