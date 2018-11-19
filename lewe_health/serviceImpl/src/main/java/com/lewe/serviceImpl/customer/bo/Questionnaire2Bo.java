package com.lewe.serviceImpl.customer.bo;

import java.util.ArrayList;
import java.util.List;

import com.lewe.bean.report.ReportSymptom;

/**
 * 问卷信息2
 * @author 小辉
 *
 */
public class Questionnaire2Bo {

	/**
	 * 采样症状列表
	 */
	private List<ReportSymptom> reportSymptomList = new ArrayList<ReportSymptom>();
	/**
	 * 目前最困扰的健康问题
	 */
	private String besetHealthProblem;
	
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
	
}
