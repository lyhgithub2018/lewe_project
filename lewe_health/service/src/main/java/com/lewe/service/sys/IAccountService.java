package com.lewe.service.sys;

import com.lewe.bean.sys.Account;
import com.alibaba.fastjson.JSONObject;

public interface IAccountService {
	/**
	 * B端系统账号登录
	 * @return
	 */
	public Account accountLogin(String account,String password,Object apiResult);

	/**
	 * 获取B端登录账号信息(包括门店/机构信息)
	 * 注:登录状态下调用
	 * @param sessionAccount
	 * @param apiResult
	 * @return
	 */
	public JSONObject getAccountAndHospitalInfo(Account sessionAccount, Object apiResult);

	/**
	 * 新增或修改B端门店账号
	 * @param accountStr
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public int saveAccount(String accountStr, Account loginAccount, Object apiResult);

	/**
	 * 删除B端账号
	 * @param accountId
	 * @param apiResult
	 * @return
	 */
	public int delAccount(String accountId,Account loginAccount, Object apiResult);
	/**
	 * 获取账号详情
	 * @param accountId
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public JSONObject getAccountDetail(Long accountId, Account loginAccount, Object apiResult);

	/**
	 * 冻结/解冻账号
	 * 注:修改账号的status字段
	 * @param accountId
	 * @param status 1:正常 2:冻结
	 * @param apiResult
	 * @return
	 */
	public int updateAccountStatus(String accountId, Integer status, Account loginAccount,Object apiResult);

	/**
	 * 分页查询账号列表
	 * @param keyword
	 * @param hospitalId
	 * @param pageNo
	 * @param pageSize
	 * @param account
	 * @param apiResult
	 * @return
	 */
	public JSONObject getAccountList(String keyword, Integer hospitalId, Integer pageNo, Integer pageSize,Account account, Object apiResult);

	/**
	 * 获取当前登录账号的权限菜单
	 * @param account
	 * @param apiResult
	 * @return
	 */
	public JSONObject getLoginAccountMenu(Account account, Object apiResult);
	
}
