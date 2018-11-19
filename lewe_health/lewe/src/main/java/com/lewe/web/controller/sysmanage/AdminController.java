package com.lewe.web.controller.sysmanage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.Channel;
import com.lewe.service.customer.ICustomerService;
import com.lewe.service.hospital.IHospitalService;
import com.lewe.service.sys.IAccountService;
import com.lewe.service.sys.IillnessService;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.StringUtils;
import com.lewe.web.controller.common.BaseController;

/**
 * 后台管理系统登录,退出登录.以及系统级数据的增删改查
 * @author 小辉
 */
@Controller
@RequestMapping("/admin/")
public class AdminController extends BaseController{
	
	@Autowired
	private IAccountService accountService;
	@Autowired
	private IHospitalService hospitalService;
	/**
	 * 登录
	 */
	@ResponseBody
	@RequestMapping("login")
	public ApiResult login(HttpServletRequest request,HttpServletResponse response) {
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		ApiResult result = new ApiResult();
		if(StringUtils.isBlank(account)) {
			result.setCode(BizCode.LOGIN_FAIL);
			result.setMessage("请输入登录账号");
			return result;
		}
		if(StringUtils.isBlank(password)) {
			result.setCode(BizCode.LOGIN_FAIL);
			result.setMessage("请输入登录密码");
			return result;
		}
		Account sysAccount = accountService.accountLogin(account, password, result);
		if(sysAccount!=null) {
			HttpSession session = request.getSession();
			session.setAttribute(SYSACCOUNT, sysAccount);
		}
		return result;
	}
	/**
	 * 退出登录
	 */
	@ResponseBody
	@RequestMapping("logout")
	public ApiResult logout(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		session.removeAttribute(SYSACCOUNT);
		ApiResult result = new ApiResult();
		result.setMessage("退出登录成功");
		return result;
	}
	/**
	 * 获取B端登录账号信息(包括门店/机构信息)
	 * 注:该接口处于登录状态时调用(因此前端可以不用传参数)
	 */
	@ResponseBody
	@RequestMapping("getAccountInfo")
	public ApiResult getAccountInfo(HttpServletRequest request,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			JSONObject json = accountService.getAccountAndHospitalInfo(account,result);
			result.setData(json);
		}
		return result;
	}
	
	/**
	 * 获取B端登录账号所能看到的权限菜单
	 */
	@ResponseBody
	@RequestMapping("getLoginAccountMenu")
	public ApiResult getLoginAccountMenu(HttpServletRequest request,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			JSONObject json = accountService.getLoginAccountMenu(account,result);
			result.setData(json);
		}
		return result;
	}
	
	/**
	 * 新增或编辑渠道
	 */
	@ResponseBody
	@RequestMapping("saveChannel")
	public ApiResult saveChannel(HttpServletRequest request,HttpServletResponse response) {
		String name = request.getParameter("name");
		String code = request.getParameter("code");
		String channelId = request.getParameter("channelId");
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			hospitalService.saveChannel(name,code,channelId,account,result);
		}
		return result;
	}
	/**
	 * 删除渠道
	 */
	@ResponseBody
	@RequestMapping("delChannel")
	public ApiResult delChannel(HttpServletRequest request,HttpServletResponse response) {
		String channelId = request.getParameter("channelId");
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			hospitalService.delChannel(channelId,account,result);
		}
		return result;
	}
	/**
	 * 渠道列表
	 */
	@ResponseBody
	@RequestMapping("getChannelList")
	public ApiResult getChannelList(HttpServletRequest request,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			String keyword = request.getParameter("keyword");
			List<Channel> list = hospitalService.getChannelList(account.getId(),keyword,result);
			result.setData(list);
		}
		return result;
	}
	@Autowired
	private IillnessService iillnessService;
	/**
	 * 保存疾病
	 */
	@ResponseBody
	@RequestMapping("saveIllness")
	public ApiResult saveIllness(HttpServletRequest request,String illnessId,String name,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			iillnessService.saveIllness(illnessId,name,account.getId(),result);
		}
		return result;
	}
	/**
	 * 删除疾病
	 */
	@ResponseBody
	@RequestMapping("delIllness")
	public ApiResult delIllness(HttpServletRequest request,Integer illnessId) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			iillnessService.delIllness(illnessId,result);
		}
		return result;
	}
	/**
	 * 查询疾病列表
	 */
	@ResponseBody
	@RequestMapping("getIllnessList")
	public ApiResult getIllnessList(HttpServletRequest request,@RequestParam(required=false,value="pageNo")Integer pageNo,@RequestParam(required=false,value="pageSize")Integer pageSize) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			JSONObject json = iillnessService.getIllnessList(pageNo,pageSize,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 保存症状
	 */
	@ResponseBody
	@RequestMapping("saveSymptom")
	public ApiResult saveSymptom(HttpServletRequest request,String symptomId,String name,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			iillnessService.saveSymptom(symptomId,name,account.getId(),result);
		}
		return result;
	}
	/**
	 * 删除症状
	 */
	@ResponseBody
	@RequestMapping("delSymptom")
	public ApiResult delSymptom(HttpServletRequest request,HttpServletResponse response) {
		String symptomId = request.getParameter("symptomId");
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			iillnessService.delSymptom(symptomId,result);
		}
		return result;
	}
	/**
	 * 查询症状列表
	 */
	@ResponseBody
	@RequestMapping("getSymptomList")
	public ApiResult getSymptomList(HttpServletRequest request,@RequestParam(required=false,value="pageNo")Integer pageNo,@RequestParam(required=false,value="pageSize")Integer pageSize) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			JSONObject json = iillnessService.getSymptomList(pageNo,pageSize);
			result.setData(json);
		}
		return result;
	}
	
	@Autowired
	private ICustomerService customerService;
	/**
	 * C端用户管理列表
	 */
	@ResponseBody
	@RequestMapping("getUserManageList")
	public ApiResult getUserManageList(HttpServletRequest request,String keyword,Byte status, Integer pageNo,Integer pageSize) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			JSONObject json = customerService.getUserManageList(keyword,status,pageNo,pageSize);
			result.setData(json);
		}
		return result;
	}
	/**
	 * C端用户管理列表--编辑修改手机号
	 */
	@ResponseBody
	@RequestMapping("updatePhone")
	public ApiResult updatePhone(HttpServletRequest request,Long id,String phone) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			customerService.updatePhone(id,phone,account,result);
		}
		return result;
	}
	/**
	 * C端用户管理列表--冻结/解冻账号
	 */
	@ResponseBody
	@RequestMapping("freezeCustomer")
	public ApiResult freezeCustomer(HttpServletRequest request,Long id,Byte status) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			customerService.freezeCustomer(id,status,account,result);
		}
		return result;
	}
}
