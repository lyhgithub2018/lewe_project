package com.lewe.bean.report;

import java.io.Serializable;
import java.util.Date;

public class ReportInfo implements Serializable {
    private Long id;

    /**
     * C端用户账号id
     */
    private Long customerId;

    /**
     * C端用户手机号
     */
    private String customerPhone;

    /**
     * 采样者编号
     */
    private String sampleCode;

    /**
     * 采样者姓名
     */
    private String sampleName;

    /**
     * 采样者本人手机号
     */
    private String samplePhone;

    /**
     * 采样者性别 1：男 2：女
     */
    private Byte sampleSex;

    /**
     * 采样者年龄
     */
    private Integer sampleAge;

    /**
     * 采样者生日
     */
    private Date sampleBirthday;

    /**
     * 采样者身高(单位:cm)
     */
    private Integer sampleHeight;

    /**
     * 采样者体重(单位:kg)
     */
    private Double sampleWeight;

    /**
     * 采样提交时间
     */
    private Date submitTime;

    /**
     * 是否提交了调查问卷 0:否 1:提交了问卷信息1  2:提交了问卷信息2
     */
    private Byte submitQuestionnaire;

    /**
     * 提交方式 1：用户提交 2：辅助提交
     */
    private Byte submitWay;

    /**
     * 送检门店所属渠道id
     */
    private Integer channelId;

    /**
     * 送检门店/机构id
     */
    private Long hospitalId;

    /**
     * 送检门店/机构 所属的门店组id
     */
    private Integer hospitalGroupId;

    /**
     * 送检科室id
     */
    private Long hospitalRoomId;

    /**
     * 送检医生id
     */
    private Long hospitalDoctorId;

    /**
     * 门店扫描日期
     */
    private Date hospitalScanTime;

    /**
     * 快递名称
     */
    private String expressName;

    /**
     * 快递单号
     */
    private String expressCode;
    /**
     * 快递单号绑定时间
     */
    private Date expressTime;

    /**
     * 检测员检测时间
     */
    private Date checkTime;

    /**
     * 检测项目id
     */
    private Integer checkItemId;

    /**
     * 检测设备id
     */
    private Integer checkDeviceId;

    /**
     * 检测底物id
     */
    private Integer checkSubstrateId;

    /**
     * 检测底物剂量(单位：ml)
     */
    private Integer checkSubstrateDosage;

    /**
     * 检测状态 0：待检测 1：已检测
     */
    private Byte checkStatus;

    /**
     * 检测员账号id
     */
    private Long checkAccountId;

    /**
     * 审核员审核时间
     */
    private Date auditTime;

    /**
     * 审核状态 0：待审核 1：已通过 2：待修改
     */
    private Byte auditStatus;

    /**
     * 审核员账号id
     */
    private Long auditAccountId;

    /**
     * 近一个月是否服用抗生素 0:否 1:是
     */
    private Byte takeAntibiotics;

    /**
     * 所服用抗生素的药品名称
     */
    private String antibioticsName;

    /**
     * 幽门螺旋杆菌检测 0:未检测 1:阳性 2:阴性
     */
    private Byte helicobacterPyloriCheck;

    /**
     * 幽门螺旋杆菌检测结果
     */
    private String hpCheckResult;

    /**
     * 胃镜/肠镜检测 0:未检测 1:胃镜 2:肠镜
     */
    private Byte gastroscopeEnteroscopyCheck;

    /**
     * 胃镜/肠镜检测结果
     */
    private String geCheckResult;

    /**
     * 食物/药物过敏 0:否 1:是
     */
    private Byte foodMedicineAllergy;

    /**
     * 过敏物品名称
     */
    private String allergyFood;

    /**
     * 困扰健康的问题
     */
    private String besetHealthProblem;

    /**
     * 气体吹气检测结果
     */
    private String gasCheckResult;

    /**
     * 气体吹气检测结果结果建议
     */
    private String gasCheckSuggestion;

    /**
     * 系统报告编号(报告编号=门店编号+设备编号+日期编号+流水编号)
     */
    private String sysReportCode;

    /**
     * 报告生成时间
     */
    private Date reportCreateTime;

    /**
     * 报告名称
     */
    private String reportName;

    /**
     * 报告状态 1：用户绑定 2：门店扫码 3：邮寄单号 4：乐为扫码 5：结果生成
     */
    private Byte reportStatus;

    /**
     * 报告结果标识 1：轻度 2：中度 3：重度 4：阳性 5：阴性
     */
    private Integer reportResult;

    /**
     * 报告结果描述
     */
    private String reportResultDescription;

    /**
     * 报告数据分析
     */
    private String reportDataAnalysis;

    /**
     * 干预建议
     */
    private String interventionSuggestion;

    /**
     * 报告图片id,若有多张逗号隔开
     */
    private String reportPictureIds;

    /**
     * 报告pdf文件id,若有多张逗号隔开
     */
    private String reportPdfIds;

    private static final long serialVersionUID = 1L;

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

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone == null ? null : customerPhone.trim();
    }

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode == null ? null : sampleCode.trim();
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName == null ? null : sampleName.trim();
    }

    public String getSamplePhone() {
        return samplePhone;
    }

    public void setSamplePhone(String samplePhone) {
        this.samplePhone = samplePhone == null ? null : samplePhone.trim();
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

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Byte getSubmitQuestionnaire() {
        return submitQuestionnaire;
    }

    public void setSubmitQuestionnaire(Byte submitQuestionnaire) {
        this.submitQuestionnaire = submitQuestionnaire;
    }

    public Byte getSubmitWay() {
        return submitWay;
    }

    public void setSubmitWay(Byte submitWay) {
        this.submitWay = submitWay;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getHospitalGroupId() {
        return hospitalGroupId;
    }

    public void setHospitalGroupId(Integer hospitalGroupId) {
        this.hospitalGroupId = hospitalGroupId;
    }

    public Long getHospitalRoomId() {
        return hospitalRoomId;
    }

    public void setHospitalRoomId(Long hospitalRoomId) {
        this.hospitalRoomId = hospitalRoomId;
    }

    public Long getHospitalDoctorId() {
        return hospitalDoctorId;
    }

    public void setHospitalDoctorId(Long hospitalDoctorId) {
        this.hospitalDoctorId = hospitalDoctorId;
    }

    public Date getHospitalScanTime() {
        return hospitalScanTime;
    }

    public void setHospitalScanTime(Date hospitalScanTime) {
        this.hospitalScanTime = hospitalScanTime;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName == null ? null : expressName.trim();
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode == null ? null : expressCode.trim();
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public Integer getCheckItemId() {
        return checkItemId;
    }

    public void setCheckItemId(Integer checkItemId) {
        this.checkItemId = checkItemId;
    }

    public Integer getCheckDeviceId() {
        return checkDeviceId;
    }

    public void setCheckDeviceId(Integer checkDeviceId) {
        this.checkDeviceId = checkDeviceId;
    }

    public Integer getCheckSubstrateId() {
        return checkSubstrateId;
    }

    public void setCheckSubstrateId(Integer checkSubstrateId) {
        this.checkSubstrateId = checkSubstrateId;
    }

    public Integer getCheckSubstrateDosage() {
        return checkSubstrateDosage;
    }

    public void setCheckSubstrateDosage(Integer checkSubstrateDosage) {
        this.checkSubstrateDosage = checkSubstrateDosage;
    }

    public Byte getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Byte checkStatus) {
        this.checkStatus = checkStatus;
    }

    public Long getCheckAccountId() {
        return checkAccountId;
    }

    public void setCheckAccountId(Long checkAccountId) {
        this.checkAccountId = checkAccountId;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Byte getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Byte auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Long getAuditAccountId() {
        return auditAccountId;
    }

    public void setAuditAccountId(Long auditAccountId) {
        this.auditAccountId = auditAccountId;
    }

    public Byte getTakeAntibiotics() {
        return takeAntibiotics;
    }

    public void setTakeAntibiotics(Byte takeAntibiotics) {
        this.takeAntibiotics = takeAntibiotics;
    }

    public String getAntibioticsName() {
        return antibioticsName;
    }

    public void setAntibioticsName(String antibioticsName) {
        this.antibioticsName = antibioticsName == null ? null : antibioticsName.trim();
    }

    public Byte getHelicobacterPyloriCheck() {
        return helicobacterPyloriCheck;
    }

    public void setHelicobacterPyloriCheck(Byte helicobacterPyloriCheck) {
        this.helicobacterPyloriCheck = helicobacterPyloriCheck;
    }

    public String getHpCheckResult() {
        return hpCheckResult;
    }

    public void setHpCheckResult(String hpCheckResult) {
        this.hpCheckResult = hpCheckResult == null ? null : hpCheckResult.trim();
    }

    public Byte getGastroscopeEnteroscopyCheck() {
        return gastroscopeEnteroscopyCheck;
    }

    public void setGastroscopeEnteroscopyCheck(Byte gastroscopeEnteroscopyCheck) {
        this.gastroscopeEnteroscopyCheck = gastroscopeEnteroscopyCheck;
    }

    public String getGeCheckResult() {
        return geCheckResult;
    }

    public void setGeCheckResult(String geCheckResult) {
        this.geCheckResult = geCheckResult == null ? null : geCheckResult.trim();
    }

    public Byte getFoodMedicineAllergy() {
        return foodMedicineAllergy;
    }

    public void setFoodMedicineAllergy(Byte foodMedicineAllergy) {
        this.foodMedicineAllergy = foodMedicineAllergy;
    }

    public String getAllergyFood() {
        return allergyFood;
    }

    public void setAllergyFood(String allergyFood) {
        this.allergyFood = allergyFood == null ? null : allergyFood.trim();
    }

    public String getBesetHealthProblem() {
        return besetHealthProblem;
    }

    public void setBesetHealthProblem(String besetHealthProblem) {
        this.besetHealthProblem = besetHealthProblem == null ? null : besetHealthProblem.trim();
    }

    public String getGasCheckResult() {
        return gasCheckResult;
    }

    public void setGasCheckResult(String gasCheckResult) {
        this.gasCheckResult = gasCheckResult == null ? null : gasCheckResult.trim();
    }

    public String getGasCheckSuggestion() {
        return gasCheckSuggestion;
    }

    public void setGasCheckSuggestion(String gasCheckSuggestion) {
        this.gasCheckSuggestion = gasCheckSuggestion == null ? null : gasCheckSuggestion.trim();
    }

    public String getSysReportCode() {
        return sysReportCode;
    }

    public void setSysReportCode(String sysReportCode) {
        this.sysReportCode = sysReportCode == null ? null : sysReportCode.trim();
    }

    public Date getReportCreateTime() {
        return reportCreateTime;
    }

    public void setReportCreateTime(Date reportCreateTime) {
        this.reportCreateTime = reportCreateTime;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName == null ? null : reportName.trim();
    }

    public Byte getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(Byte reportStatus) {
        this.reportStatus = reportStatus;
    }

    public Integer getReportResult() {
        return reportResult;
    }

    public void setReportResult(Integer reportResult) {
        this.reportResult = reportResult;
    }

    public String getReportResultDescription() {
        return reportResultDescription;
    }

    public void setReportResultDescription(String reportResultDescription) {
        this.reportResultDescription = reportResultDescription == null ? null : reportResultDescription.trim();
    }

    public String getReportDataAnalysis() {
        return reportDataAnalysis;
    }

    public void setReportDataAnalysis(String reportDataAnalysis) {
        this.reportDataAnalysis = reportDataAnalysis == null ? null : reportDataAnalysis.trim();
    }

    public String getInterventionSuggestion() {
        return interventionSuggestion;
    }

    public void setInterventionSuggestion(String interventionSuggestion) {
        this.interventionSuggestion = interventionSuggestion == null ? null : interventionSuggestion.trim();
    }

    public String getReportPictureIds() {
        return reportPictureIds;
    }

    public void setReportPictureIds(String reportPictureIds) {
        this.reportPictureIds = reportPictureIds == null ? null : reportPictureIds.trim();
    }

    public String getReportPdfIds() {
        return reportPdfIds;
    }

    public void setReportPdfIds(String reportPdfIds) {
        this.reportPdfIds = reportPdfIds == null ? null : reportPdfIds.trim();
    }

	public Date getExpressTime() {
		return expressTime;
	}

	public void setExpressTime(Date expressTime) {
		this.expressTime = expressTime;
	}
    
}