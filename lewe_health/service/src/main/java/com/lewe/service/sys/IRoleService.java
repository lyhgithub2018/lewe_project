package com.lewe.service.sys;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;

public interface IRoleService {
	/**
	 * 保存角色
	 * @param roleName
	 * @param menuIds
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public int saveRole(String roleId,String roleName, String menuIds, Account loginAccount, Object apiResult);

	/**
	 * 获取系统权限菜单
	 * @return
	 */
	public JSONObject getSysMenuTree();

	/**
	 * 删除角色
	 * @param roleId
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public int delRole(String roleId, Account loginAccount, Object apiResult);

	/**
	 * 分页查角色列表
	 * @param pageNo
	 * @param pageSize
	 * @param account
	 * @return
	 */
	public JSONObject getRoleList(Integer pageNo, Integer pageSize, Account account);

	/**
	 * 编辑时的角色详情
	 * @param roleId
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public JSONObject getRoleDetail(Integer roleId, Account loginAccount, Object apiResult);
}
