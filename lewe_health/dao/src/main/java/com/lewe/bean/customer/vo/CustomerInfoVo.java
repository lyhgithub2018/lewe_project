package com.lewe.bean.customer.vo;

import java.util.ArrayList;
import java.util.List;

import com.lewe.bean.report.ReportSymptom;

/**
 * 
 * @author 客户信息对象
 * 注:用于页面展示数据
 */
public class CustomerInfoVo {
	private Long id;//客户报告信息主键id
	/**
	 * 送检门店id
	 */
	private Long hospitalId;
	
	private String hospitalName;

    private String sampleCode;//采样者编号

    private String sampleName;//采样者姓名

    private String samplePhone;//采样者手机号

    private Byte sampleSex;//采样者性别

    private Integer sampleAge;//采样者年龄

    private Integer sampleHeight;//采样者身高

    private Double sampleWeight;//采样者体重
    
    /**
     * 提交方式 1：用户提交 2：辅助提交
     */
    private Byte submitWay;
    private String submitWayName;
    
    /**
     * 报告状态 1：用户绑定 2：门店扫码 3：邮寄单号 4：乐为扫码 5：结果生成
     */
    private Byte reportStatus;
    
    private String reportStatusName;//报告状态含义描述
    
    /**
	 * 采样症状列表
	 */
	private List<ReportSymptom> reportSymptomList = new ArrayList<ReportSymptom>();
	/**
	 * 目前最困扰的健康问题
	 */
	private String besetHealthProblem;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Long hospitalId) {
		this.hospitalId = hospitalId;
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
	public Byte getSubmitWay() {
		return submitWay;
	}
	public void setSubmitWay(Byte submitWay) {
		this.submitWay = submitWay;
	}
	public Byte getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(Byte reportStatus) {
		this.reportStatus = reportStatus;
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
	
	public String getReportStatusName() {
		return reportStatusName;
	}
	public void setReportStatusName(String reportStatusName) {
		this.reportStatusName = reportStatusName;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getSubmitWayName() {
		return submitWayName;
	}
	public void setSubmitWayName(String submitWayName) {
		this.submitWayName = submitWayName;
	}
}
