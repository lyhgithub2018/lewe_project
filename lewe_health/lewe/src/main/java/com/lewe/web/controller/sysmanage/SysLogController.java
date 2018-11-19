package com.lewe.web.controller.sysmanage;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;
import com.lewe.service.sys.ISysLogService;
import com.lewe.util.common.ApiResult;
import com.lewe.web.controller.common.BaseController;

@Controller
@RequestMapping("/sysLog/")
public class SysLogController extends BaseController{
	@Autowired
	private ISysLogService sysLogService;
	/**
	 * 系统操作日志列表查询
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getLogList")
	public ApiResult getLogList(HttpServletRequest request,String keyword,String operateDate,Integer pageNo, Integer pageSize,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = sysLogService.getLogList(keyword,operateDate,pageNo,pageSize,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
}
