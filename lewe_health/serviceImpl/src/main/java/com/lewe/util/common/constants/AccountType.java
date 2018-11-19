package com.lewe.util.common.constants;


/**
 * 账号类型
 * 注：对应sys_account表中的account_type字段
 * @author 小辉
 */
public enum AccountType {
	
	SUPERADMIN(1, "超级管理员"), 
	GROUPADMIN(2, "门店组管理员"), 
	HOSPITALADMIN(3,"门店管理员");
	
	private final int value;
	private final String desc;

	private AccountType(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public int getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}
}
