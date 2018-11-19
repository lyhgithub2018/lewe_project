package com.lewe.web.controller.sysmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;
import com.lewe.service.sys.IAccountService;
import com.lewe.service.sys.IRoleService;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.StringUtils;
import com.lewe.web.controller.common.BaseController;

/**
 * B端账号管理和权限管理
 * @author 小辉
 */
@Controller
@RequestMapping("/account/")
public class AccountController extends BaseController{
	@Autowired
	private IAccountService accountService;
	@Autowired
	private IRoleService roleService;
	
	/**
	 * 新增或修改 账号
	 */
	@ResponseBody
	@RequestMapping("saveAccount")
	public ApiResult saveAccount(HttpServletRequest request,HttpServletResponse response) {
		String accountStr = request.getParameter("account");
		ApiResult result = new ApiResult();
		if(StringUtils.isBlank(accountStr)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请求参数为空");
			return result;
		}
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			accountService.saveAccount(accountStr,loginAccount,result);
		}
		return result;
	}
	/**
	 * 删除账号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delAccount")
	public ApiResult delAccount(HttpServletRequest request,HttpServletResponse response) {
		String accountId = request.getParameter("accountId");
		ApiResult result = new ApiResult();
		if(StringUtils.isBlank(accountId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的账号");
			return result;
		}
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			accountService.delAccount(accountId,account,result);
		}
		return result;
	}
	@ResponseBody
	@RequestMapping("getAccountDetail")
	public ApiResult getAccountDetail(HttpServletRequest request,Long accountId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = accountService.getAccountDetail(accountId,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 冻结或解冻B端账号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("freezeAccount")
	public ApiResult freezeAccount(HttpServletRequest request,HttpServletResponse response) {
		String accountId = request.getParameter("accountId");
		//1:解冻 2:冻结
		String freezeType = request.getParameter("freezeType");
		ApiResult result = new ApiResult();
		if(StringUtils.isBlank(accountId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要操作的账号");
			return result;
		}
		if(StringUtils.isBlank(freezeType)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数freezeType不可为空");
			return result;
		}
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			accountService.updateAccountStatus(accountId,Integer.valueOf(freezeType),account,result);
		}
		return result;
	}
	/**
	 * 分页查询账号列表
	 */
	@ResponseBody
	@RequestMapping("getAccountList")
	public ApiResult getAccountList(HttpServletRequest request,@RequestParam(required=false,value="pageNo")Integer pageNo,@RequestParam(required=false,value="pageSize")Integer pageSize,
			@RequestParam(required = false,value="hospitalId") Integer hospitalId) {
		String keyword = request.getParameter("keyword");
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			JSONObject json = accountService.getAccountList(keyword,hospitalId,pageNo,pageSize,account,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 获取系统权限菜单树
	 */
	@ResponseBody
	@RequestMapping("getSysMenuTree")
	public ApiResult getSysMenuTree(HttpServletRequest request,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			result.setData(roleService.getSysMenuTree());
		}
		return result;
	}
	/**
	 * 新增或修改 角色
	 */
	@ResponseBody
	@RequestMapping("saveRole")
	public ApiResult saveRole(HttpServletRequest request,HttpServletResponse response) {
		String roleId = request.getParameter("roleId");
		String roleName = request.getParameter("roleName");
		String menuIds = request.getParameter("menuIds");
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			roleService.saveRole(roleId,roleName,menuIds,loginAccount,result);
		}
		return result;
	}
	/**
	 * 角色详情(前端编辑时调用)
	 */
	@ResponseBody
	@RequestMapping("getRoleDetail")
	public ApiResult getRoleDetail(HttpServletRequest request,Integer roleId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = roleService.getRoleDetail(roleId,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 删除 角色
	 */
	@ResponseBody
	@RequestMapping("delRole")
	public ApiResult delRole(HttpServletRequest request,HttpServletResponse response) {
		String roleId = request.getParameter("roleId");
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			roleService.delRole(roleId,loginAccount,result);
		}
		return result;
	}
	/**
	 * 分页查询角色列表
	 */
	@ResponseBody
	@RequestMapping("getRoleList")
	public ApiResult getRoleList(HttpServletRequest request,@RequestParam(required=false,value="pageNo")Integer pageNo,@RequestParam(required=false,value="pageSize")Integer pageSize) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request,result);
		if(account!=null) {
			JSONObject json = roleService.getRoleList(pageNo,pageSize,account);
			result.setData(json);
		}
		return result;
	}
}
