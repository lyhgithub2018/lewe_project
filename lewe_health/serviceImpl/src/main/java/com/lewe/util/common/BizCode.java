package com.lewe.util.common;

public class BizCode {
	public static final int REQUEST_SUCCESS = 0;//请求成功
	public static final int REQUEST_FAIL = 1;//请求失败
	public static final int LOGIN_SUCCESS = 1000;//登录成功
	public static final int LOGIN_FAIL = 1001;//登录失败
	public static final int LOGIN_TIMEOUT = 1002;//登录超时
	 
	public static final int PARAM_EMPTY = 2001;//参数为空
	public static final int DATA_NOT_FOUND = 2002;//未查询到数据
	public static final int DATA_EXIST = 2003;//数据已存在
	public static final int NOT_ALLOW_DEL = 2004;//不允许删除数据
	public static final int DATA_INCORRECT = 2005;//数据不正确
	
	/**
	 * 获取状态表示的含义
	 * @param code
	 * @return
	 */
	public static String getCodeMsg(int code) {
		String codeMsg = "";
		switch (code) {
		case REQUEST_SUCCESS:
			codeMsg = "请求成功";
			break;
		case REQUEST_FAIL:
			codeMsg = "请求失败";
			break;
		case LOGIN_SUCCESS:
			codeMsg = "登录成功";
			break;
		case LOGIN_FAIL:
			codeMsg = "登录失败";
			break;
		case LOGIN_TIMEOUT:
			codeMsg = "登录超时,请重新登录";
			break;
		case PARAM_EMPTY:
			codeMsg = "请求参数为空";
			break;
		case DATA_NOT_FOUND:
			codeMsg = "未查询到数据";
			break;
		case DATA_EXIST:
			codeMsg = "数据已存在";
			break;
		case NOT_ALLOW_DEL:
			codeMsg = "不允许删除数据";
			break;
		case DATA_INCORRECT:
			codeMsg = "数据不正确";
			break;
		default:
			break;
		}
		return codeMsg;
	 }
}
