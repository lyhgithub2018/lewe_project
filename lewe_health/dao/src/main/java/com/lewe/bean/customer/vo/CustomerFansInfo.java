package com.lewe.bean.customer.vo;

/**
 * 系统管理--用户管理列表
 * 注:页面展示数据用
 * @author 小辉
 *
 */
public class CustomerFansInfo {
	private Long id;
	private String phone;//手机号
	private String nickName;//微信昵称
	private String headImgUrl;//微信头像
	private Byte status;//账号状态 1：正常 2：冻结
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getHeadImgUrl() {
		return headImgUrl;
	}
	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	
}
