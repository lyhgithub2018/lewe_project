package com.lewe.web.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lewe.bean.sys.Account;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;

public class BaseController {
	
	//B端后台登录用户的session attribute name
	public static final String SYSACCOUNT = "sysAccount";
	public static final Logger log = LoggerFactory.getLogger(BaseController.class); 
	
	/**
	 * 从session中获取B端登录账号信息
	 * @param request
	 * @return
	 */
	public Account getSessionAccount(HttpServletRequest request,ApiResult result) {
		Account account = null;
		//本地开发阶段前端调用接口(给个固定账号免登陆)
		String sysType = System.getProperties().getProperty("os.name");
		if (sysType.toLowerCase().startsWith("win")) {
			account = new Account();
			account.setId(1l);
			account.setAccount("admin");
			account.setName("超级管理员");
			account.setRoleId(1);
			account.setHospitalId(1l);
			account.setHospitalGroupId(1);
			account.setStatus(1);
			account.setAccountType((byte)1);
			
			/*account.setId(9l);
			account.setAccount("小灰灰01");
			account.setName("xiaohui01");
			account.setRoleId(11);
			account.setHospitalId(5l);
			account.setHospitalGroupId(1);
			account.setStatus(1);
			account.setAccountType((byte)3);*/
		}else {
			//真实运行环境下
			HttpSession session = request.getSession();
			Object obj = session.getAttribute(SYSACCOUNT);
			if(obj==null) {
				result.setCode(BizCode.LOGIN_TIMEOUT);
				result.setMessage("账号未登录或登录超时");
				return null;
			}
			account = (Account)obj;
		}
		return account;
	}
}
