package com.lewe.util.common;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @Description:api接口返回结果
 * @author 小辉
 * @date 2018年10月24日
 *
 */
public class ApiResult {
	/**
	 * 接口错误码,默认0:表示请求成功
	 */
	private int code = 0;
	
	/**
	 * 接口的具体返回数据
	 */
	private Object data = new JSONObject();
	
	/**
	 * 接口返回信息,默认请求成功
	 */
	private String message = "操作成功";

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
