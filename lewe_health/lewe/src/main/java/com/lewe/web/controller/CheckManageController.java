package com.lewe.web.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;
import com.lewe.service.check.ICheckManageService;
import com.lewe.util.common.ApiResult;
import com.lewe.web.controller.common.BaseController;

/**
 * 检测管理的控制器
 * @author 小辉
 *
 */
@Controller
@RequestMapping("/checkManage/")
public class CheckManageController extends BaseController{
	
	@Autowired
	private ICheckManageService checkManageService;
	/**
	 * 检测样品列表查询
	 */
	@ResponseBody
	@RequestMapping("checkSampleList")
	public ApiResult checkSampleList(HttpServletRequest request,String sampleInfoQuery,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = checkManageService.checkSampleList(sampleInfoQuery,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 检测样品详情
	 */
	@ResponseBody
	@RequestMapping("checkInfoDetail")
	public ApiResult checkInfoDetail(HttpServletRequest request,Long id,String sampleCode,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = checkManageService.checkInfoDetail(id,sampleCode,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 提交检测信息
	 */
	@ResponseBody
	@RequestMapping(value="submitCheckInfo",method=RequestMethod.POST)
	public ApiResult submitCheckInfo(HttpServletRequest request,String checkInfo,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			checkManageService.submitCheckInfo(checkInfo,loginAccount,result);
		}
		return result;
	}
	/**
	 * 保存检测设备
	 */
	@ResponseBody
	@RequestMapping("saveCheckDevice")
	public ApiResult saveCheckDevice(HttpServletRequest request,Integer id,String name,String code,Long hospitalId) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			checkManageService.saveCheckDevice(id,name,code,hospitalId,account,result);
		}
		return result;
	}
	/**
	 * 删除检测设备
	 */
	@ResponseBody
	@RequestMapping("delCheckDevice")
	public ApiResult delCheckDevice(HttpServletRequest request,Integer deviceId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			checkManageService.delCheckDevice(deviceId,account,result);
		}
		return result;
	}
	/**
	 * 检测设备列表
	 */
	@ResponseBody
	@RequestMapping("getCheckDeviceList")
	public ApiResult getCheckDeviceList(HttpServletRequest request,Long hospitalId,Integer pageNo,Integer pageSize) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			JSONObject json = checkManageService.getCheckDeviceList(hospitalId,pageNo,pageSize,account,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 保存检测项目
	 */
	@ResponseBody
	@RequestMapping("saveCheckItem")
	public ApiResult saveCheckItem(HttpServletRequest request,String checkItem,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			checkManageService.saveCheckItem(checkItem,account,result);
		}
		return result;
	}
	/**
	 * 删除检测项目
	 */
	@ResponseBody
	@RequestMapping("delCheckItem")
	public ApiResult delCheckItem(HttpServletRequest request,String itemId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			checkManageService.delCheckItem(itemId,account,result);
		}
		return result;
	}
	/**
	 * 获取某个检测项目对应的一组底物和剂量
	 * @param itemId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getItemSubstrateList")
	public ApiResult getItemSubstrateList(HttpServletRequest request,Integer itemId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			result.setData(checkManageService.getItemSubstrateList(itemId,account,result));
		}
		return result;
	}
	/**
	 * 校验检测项目中的底物是否在使用中
	 * @param request
	 * @param itemId
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("checkSubstrateIsUsed")
	public ApiResult checkSubstrateIsUsed(HttpServletRequest request,Integer itemId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			JSONObject json = checkManageService.checkSubstrateIsUsed(itemId,account,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 检测项目列表
	 */
	@ResponseBody
	@RequestMapping("getCheckItemList")
	public ApiResult getCheckItemList(HttpServletRequest request,String keyword,Integer pageNo,Integer pageSize) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			JSONObject json = checkManageService.getCheckItemList(keyword,pageNo,pageSize,account,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 检测审核列表查询
	 * @param request
	 * @param sampleInfoQuery
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("checkAuditList")
	public ApiResult checkAuditList(HttpServletRequest request,String sampleInfoQuery,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = checkManageService.checkAuditList(sampleInfoQuery,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	
	/**
	 * 提交审核信息(对应页面'审核通过'操作)
	 */
	@ResponseBody
	@RequestMapping(value="submitAuditInfo",method=RequestMethod.POST)
	public ApiResult submitAuditInfo(HttpServletRequest request,String auditInfo,Integer operateType,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			checkManageService.submitAuditInfo(auditInfo,operateType,loginAccount,result);
		}
		return result;
	}
	
	/**
	 * 获取审核信息详情
	 */
	@ResponseBody
	@RequestMapping("auditInfoDetail")
	public ApiResult auditInfoDetail(HttpServletRequest request,Long id,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = checkManageService.auditInfoDetail(id,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 待修改操作
	 * 注:将审核状态更改为待修改,该接口暂时不用了(由于待修改操作也是需要更新字段信息的,因此将该接口移至审核接口中)
	 */
	@ResponseBody
	@RequestMapping("updateAuditStatus")
	public ApiResult updateAuditStatus(HttpServletRequest request,Long id,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			checkManageService.updateAuditStatus(id,loginAccount,result);
		}
		return result;
	}
	
	/**
	 * 获取检测页面或审核页面或报告页面中需要展示的字段
	 * @param reportId 报告信息主键id(C端报告详情中传该参数)
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getShowFieldList")
	public ApiResult getShowFieldList(HttpServletRequest request,Long reportId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		JSONObject json = null;
		if(reportId!=null) {
			json = checkManageService.getShowFieldList(reportId,null,result);
		}else {
			Account loginAccount = getSessionAccount(request,result);
			json = checkManageService.getShowFieldList(reportId,loginAccount,result);
		}
		result.setData(json);
		return result;
	}
}
