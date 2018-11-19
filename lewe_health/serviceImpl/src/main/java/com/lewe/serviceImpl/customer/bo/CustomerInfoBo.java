package com.lewe.serviceImpl.customer.bo;

import java.util.ArrayList;
import java.util.List;

import com.lewe.bean.report.ReportSymptom;

/**
 * 新增或修改客户信息的对象
 * @author 小辉
 *
 */
public class CustomerInfoBo {
	
	private Long id;//客户报告信息主键id
	
	private Long customerId;//C端登录客户的id

    private String sampleCode;//采样者编号

    private String sampleName;//采样者姓名

    private String samplePhone;//采样者电话

    private Byte sampleSex;//采样者性别

    private Integer sampleAge;//采样者年龄

    private Integer sampleHeight;//采样者身高

    private Double sampleWeight;//采样者体重
    
    /**
	 * 采样症状列表
	 */
	private List<ReportSymptom> reportSymptomList = new ArrayList<ReportSymptom>();
	/**
	 * 目前最困扰的健康问题
	 */
	private String besetHealthProblem;
	/**
	 * 送检门店id
	 */
	private Long hospitalId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public Integer getSampleAge() {
		return sampleAge;
	}
	public void setSampleAge(Integer sampleAge) {
		this.sampleAge = sampleAge;
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
	public List<ReportSymptom> getReportSymptomList() {
		return reportSymptomList;
	}
	public void setReportSymptomList(List<ReportSymptom> reportSymptomList) {
		this.reportSymptomList = reportSymptomList;
	}
	public String getBesetHealthProblem() {
		return besetHealthProblem;
	}
	public void setBesetHealthProblem(String besetHealthProblem) {
		this.besetHealthProblem = besetHealthProblem;
	}
	public Long getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Long hospitalId) {
		this.hospitalId = hospitalId;
	}
	
}
