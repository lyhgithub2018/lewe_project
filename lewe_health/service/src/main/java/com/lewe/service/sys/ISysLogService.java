package com.lewe.service.sys;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;

public interface ISysLogService {
	/**
	 * 记录当前登录账号所进行的操作
	 * @param loginAccount
	 * @param content     操作内容
	 * @param operateTime 操作时间
	 */
	public void addSysLog(Account loginAccount,String content,Date operateTime);

	/**
	 * B端系统日志列表查询
	 * @param keyword
	 * @param operateDate
	 * @param loginAccount
	 * @param ApiResult
	 * @return
	 */
	public JSONObject getLogList(String keyword, String operateDate,Integer pageNo, Integer pageSize, Account loginAccount, Object ApiResult);
}
