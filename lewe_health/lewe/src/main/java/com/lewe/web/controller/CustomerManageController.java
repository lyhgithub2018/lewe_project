package com.lewe.web.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;
import com.lewe.service.customer.ICustomerManageService;
import com.lewe.util.common.ApiResult;
import com.lewe.web.controller.common.BaseController;

/**
 * 客户管理相关的控制器
 * @author 小辉
 */
@Controller
@RequestMapping("/customerManager/")
public class CustomerManageController extends BaseController{
	@Autowired
	private ICustomerManageService customerManageService;
	
	/**
	 * 客户信息列表
	 */
	@ResponseBody
	@RequestMapping("getCustomerInfoList")
	public ApiResult getCustomerInfoList(HttpServletRequest request,HttpServletResponse response) {
		String customerInfoQuery = request.getParameter("customerInfoQuery");
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = customerManageService.getCustomerInfoList(customerInfoQuery,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 新增或修改客户信息
	 */
	@ResponseBody
	@RequestMapping("addOrUpdateCustomerInfo")
	public ApiResult addOrUpdateCustomerInfo(HttpServletRequest request,Integer operateType,HttpServletResponse response) {
		String customerInfo = request.getParameter("customerInfo");
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			customerManageService.addOrUpdateCustomerInfo(operateType,customerInfo,loginAccount,result);
		}
		return result;
	}
	/**
	 * 获取客户信息详情
	 */
	@ResponseBody
	@RequestMapping("getCustomerInfo")
	public ApiResult getCustomerInfo(HttpServletRequest request,Long customerInfoId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			result.setData(customerManageService.getCustomerInfo(customerInfoId,loginAccount,result));
		}
		return result;
	}
	/**
	 * 检查当前编号是否扫描过
	 */
	@ResponseBody
	@RequestMapping("checkIsHadScan")
	public ApiResult checkIsHadScan(HttpServletRequest request,String scanCode,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			result.setData(customerManageService.checkIsHadScan(scanCode,loginAccount,result));
		}
		return result;
	}
	/**
	 * 确定提交扫描出来的编号
	 * @param codeArray 编号数组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("submitScanCodes")
	public ApiResult submitScanCodes(HttpServletRequest request,String codeArray,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			customerManageService.submitScanCodes(codeArray,loginAccount,result);
		}
		return result;
	}
	/**
	 * 新增快递信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("addExpressInfo")
	public ApiResult addExpressInfo(HttpServletRequest request,String expressName,String expressCode,String customerInfoIds,Integer channelId,Long hospitalId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			customerManageService.addExpressInfo(expressName,expressCode,customerInfoIds,channelId,hospitalId,loginAccount,result);
		}
		return result;
	}
	/**
	 * 快递信息列表查询
	 */
	@ResponseBody
	@RequestMapping("expressInfoList")
	public ApiResult expressInfoList(HttpServletRequest request,String keyword,Integer channelId,Integer pageNo,Integer pageSize,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = customerManageService.expressInfoList(keyword,channelId,pageNo,pageSize,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 快递信息详情
	 */
	@ResponseBody
	@RequestMapping("expressInfoDetail")
	public ApiResult expressInfoDetail(HttpServletRequest request,Long expressId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = customerManageService.expressInfoDetail(expressId,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 新增快递时选择客户下拉列表
	 */
	@ResponseBody
	@RequestMapping("customerSelectList")
	public ApiResult customerSelectList(HttpServletRequest request,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			result.setData(customerManageService.customerSelectList(loginAccount,result));
		}
		return result;
	}
}
