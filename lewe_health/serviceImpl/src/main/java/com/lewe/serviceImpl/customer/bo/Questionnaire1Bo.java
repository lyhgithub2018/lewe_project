package com.lewe.serviceImpl.customer.bo;

import java.util.Date;
/**
 * 问卷信息1
 * @author 小辉
 *
 */
public class Questionnaire1Bo {
	private Long customerId;//C端登录客户的id

    private String sampleCode;//采样者编号

    private String sampleName;//采样者姓名

    private String samplePhone;//采样者电话

    private Byte sampleSex;//采样者性别

    private Date sampleBirthday;//采样者生日

    private Integer sampleHeight;//采样者身高

    private Double sampleWeight;//采样者体重

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getSampleCode() {
		return sampleCode;
	}

	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getSamplePhone() {
		return samplePhone;
	}

	public void setSamplePhone(String samplePhone) {
		this.samplePhone = samplePhone;
	}

	public Byte getSampleSex() {
		return sampleSex;
	}

	public void setSampleSex(Byte sampleSex) {
		this.sampleSex = sampleSex;
	}

	public Date getSampleBirthday() {
		return sampleBirthday;
	}

	public void setSampleBirthday(Date sampleBirthday) {
		this.sampleBirthday = sampleBirthday;
	}

	public Integer getSampleHeight() {
		return sampleHeight;
	}

	public void setSampleHeight(Integer sampleHeight) {
		this.sampleHeight = sampleHeight;
	}

	public Double getSampleWeight() {
		return sampleWeight;
	}

	public void setSampleWeight(Double sampleWeight) {
		this.sampleWeight = sampleWeight;
	}
}
