package com.lewe.serviceImpl.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.check.CheckDevice;
import com.lewe.bean.check.CheckItem;
import com.lewe.bean.check.Substrate;
import com.lewe.bean.hospital.Hospital;
import com.lewe.bean.hospital.HospitalDoctor;
import com.lewe.bean.hospital.HospitalRoom;
import com.lewe.bean.report.ReportIllness;
import com.lewe.bean.report.ReportInfo;
import com.lewe.bean.report.ReportSymptom;
import com.lewe.bean.report.vo.UsedCountInfo;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.Channel;
import com.lewe.bean.sys.Illness;
import com.lewe.bean.sys.Role;
import com.lewe.bean.sys.Symptom;
import com.lewe.bean.sys.SysFile;
import com.lewe.dao.check.CheckDeviceMapper;
import com.lewe.dao.check.CheckItemMapper;
import com.lewe.dao.check.SubstrateMapper;
import com.lewe.dao.hospital.HospitalDoctorMapper;
import com.lewe.dao.hospital.HospitalMapper;
import com.lewe.dao.hospital.HospitalRoomMapper;
import com.lewe.dao.report.ReportIllnessMapper;
import com.lewe.dao.report.ReportInfoMapper;
import com.lewe.dao.report.ReportSymptomMapper;
import com.lewe.dao.report.UsedCountMapper;
import com.lewe.dao.sys.AccountMapper;
import com.lewe.dao.sys.ChannelMapper;
import com.lewe.dao.sys.IllnessMapper;
import com.lewe.dao.sys.RoleMapper;
import com.lewe.dao.sys.SymptomMapper;
import com.lewe.dao.sys.SysFileMapper;
import com.lewe.service.customer.ICustomerManageService;
import com.lewe.service.report.IReportInfoService;
import com.lewe.serviceImpl.report.bo.ReportCountExcel;
import com.lewe.serviceImpl.report.bo.UsedCountExcel;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.DateUtil;
import com.lewe.util.common.Page;
import com.lewe.util.common.StringUtils;
import com.lewe.util.common.constants.AccountType;
import com.lewe.util.common.constants.ReportStatus;
import com.lewe.util.common.excel.ExcelUtil;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reportInfoService")
public class ReportInfoServiceImpl implements IReportInfoService {
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private CheckItemMapper checkItemMapper;
	@Autowired
	private ReportInfoMapper reportInfoMapper;
	@Autowired
	private ReportIllnessMapper reportIllnessMapper;
	@Autowired
	private SysFileMapper sysFileMapper;
	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private CheckDeviceMapper checkDeviceMapper;

	@Autowired
	private ReportSymptomMapper reportSymptomMapper;
	@Autowired
	private HospitalMapper hospitalMapper;

	@Autowired
	private SubstrateMapper substrateMapper;

	@Autowired
	private ICustomerManageService customerManageService;

	@Autowired
	private HospitalRoomMapper hospitalRoomMapper;

	@Autowired
	private HospitalDoctorMapper hospitalDoctorMapper;

	@Autowired
	private SymptomMapper symptomMapper;

	@Autowired
	private IllnessMapper illnessMapper;

	public static final Logger logger = LoggerFactory.getLogger(ReportInfoServiceImpl.class);

	public JSONObject reportCountList(String reportInfoQuery, Account loginAccount, Object apiResult) {
		JSONObject json = new JSONObject();
		Map<String, Object> paramMap = null;
		if (StringUtils.isNotBlank(reportInfoQuery)) {
			paramMap = jsonToMap(reportInfoQuery);
		} else {
			paramMap = new HashMap<String, Object>();
		}
		paramMap.put("reportStatus", ReportStatus.RESULT_CREATE.getValue());

		List<Long> hospitalIds = customerManageService.getUserHostList(loginAccount);

		// 这里做权限判定
		if (hospitalIds == null) {
			// 主账号
		} else if (hospitalIds.size() == 0) {
			// 无权限
			hospitalIds.add(0L);
			paramMap.put("hospitalIdList", hospitalIds);
		} else {
			// 非主账号，有权限
			paramMap.put("hospitalIdList", hospitalIds);
		}

		String keyword = "";
		Object obj = paramMap.get("keyword");
		if (StringUtils.isNotBlank(obj)) {
			keyword = obj.toString().replaceAll("\\s*", "");
			paramMap.put("keyword", "%" + keyword + "%");
		} else {
			paramMap.put("keyword", null);
		}

		// 判断是否有illnessId和illnessDegree这两个参数查询
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(paramMap.get("illnessId"))) {
			map.put("illnessId", paramMap.get("illnessId"));
		}
		if (StringUtils.isNotBlank(paramMap.get("illnessDegree"))) {
			map.put("illnessDegree", paramMap.get("illnessDegree"));
		}
		if (map.size() > 0) {
			List<ReportIllness> illnessList = reportIllnessMapper.selectListByMap(map);
			if (illnessList != null && illnessList.size() > 0) {
				List<Long> reportInfoIdList = new ArrayList<Long>();
				for (ReportIllness reportIllness : illnessList) {
					reportInfoIdList.add(reportIllness.getReportInfoId());
				}
				map.put("reportInfoIdList", reportInfoIdList);
			}
		}
		Integer totalCount = reportInfoMapper.selectCountByMap(paramMap);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("reportCountList", null);
			return json;
		}
		int pageNo = 1;
		int pageSize = 10;
		if (StringUtils.isNotBlank(paramMap.get("pageNo"))) {
			pageNo = Integer.valueOf(paramMap.get("pageNo").toString());
		}
		if (StringUtils.isNotBlank(paramMap.get("pageSize"))) {
			pageSize = Integer.valueOf(paramMap.get("pageSize").toString());
		}
		Page page = new Page(pageNo, pageSize, totalCount);

		paramMap.put("startIndex", page.getStartIndex());
		paramMap.put("orderBy", "audit_time");

		List<ReportInfo> list = reportInfoMapper.selectListByMap(paramMap);

		List<JSONObject> reportCountList = new ArrayList<JSONObject>();
		for (ReportInfo reportInfo : list) {
			JSONObject sampleInfo = new JSONObject();
			sampleInfo.put("id", reportInfo.getId());
			sampleInfo.put("sampleCode", reportInfo.getSampleCode());// 采样编号
			sampleInfo.put("sampleName", reportInfo.getSampleName());// 采样者姓名
			sampleInfo.put("samplePhone", reportInfo.getSamplePhone());// 采样者手机号
			if (reportInfo.getSampleSex() != null) {// 采样者性别
				if (reportInfo.getSampleSex() == 1) {
					sampleInfo.put("sampleSex", "男");
				} else if (reportInfo.getSampleSex() == 2) {
					sampleInfo.put("sampleSex", "女");
				}
			}
			sampleInfo.put("sampleAge", reportInfo.getSampleAge());// 采样者年龄
			Account account = accountMapper.selectByPrimaryKey(reportInfo.getCheckAccountId());
			sampleInfo.put("checkerName", account == null ? "" : account.getName());// 检测员
			sampleInfo.put("sysReportCode", reportInfo.getSysReportCode());// 报告编号
			sampleInfo.put("reportCreateTime", reportInfo.getAuditTime() == null ? null
					: DateUtil.formatDate(reportInfo.getAuditTime(), "yyyy-MM-dd"));// 出报告时间

			// 查询检测项目的名称
			CheckItem checkItem = checkItemMapper.selectByPrimaryKey(reportInfo.getCheckItemId());
			sampleInfo.put("checkItemName", checkItem == null ? null : "【" + checkItem.getName() + "】");// 检测项目名称
			reportCountList.add(sampleInfo);
		}
		json.put("page", page);
		json.put("reportCountList", reportCountList);

		String hasExport = "1";
		if (loginAccount.getRoleId() != null) {
			Role rf = roleMapper.selectByPrimaryKey(loginAccount.getRoleId());
			if (rf.getName() != null && rf.getName().indexOf("管理员") >= 0) {
				hasExport = "1";
			} else {
				hasExport = "0";
			}
		}

		json.put("hasExport", hasExport);
		return json;
	}

	/**
	 * 将json格式的参数转成map
	 * 
	 * @param json
	 * @return
	 */
	private Map<String, Object> jsonToMap(String json) {
		if (StringUtils.isNotBlank(json)) {
			JSONObject jsonObject = JSONObject.parseObject(json);

			Map<String, Object> valueMap = new HashMap<String, Object>();

			// 去掉空的
			for (String key : jsonObject.keySet()) {
				if (jsonObject.get(key) == null || StringUtils.isBlank(jsonObject.get(key).toString())) {
					continue;
				}
				valueMap.put(key, jsonObject.get(key));
			}

			return valueMap;
		} else {
			return new HashMap<String, Object>();
		}
	}

	@Autowired
	private ChannelMapper channelMapper;
	@Autowired
	private UsedCountMapper usedCountMapper;

	public JSONObject usedCountList(String usedCountQuery, Account loginAccount, Object apiResult) {
		JSONObject json = new JSONObject();
		Map<String, Object> paramMap = null;
		if (StringUtils.isNotBlank(usedCountQuery)) {
			paramMap = jsonToMap(usedCountQuery);
		} else {
			paramMap = new HashMap<String, Object>();
		}

		List<Long> hospitalIds = customerManageService.getUserHostList(loginAccount);

		// 这里做权限判定
		if (hospitalIds == null) {
			// 主账号
		} else if (hospitalIds.size() == 0) {
			// 无权限
			hospitalIds.add(0L);
			paramMap.put("hospitalIdList", hospitalIds);
		} else {
			// 非主账号，有权限
			paramMap.put("hospitalIdList", hospitalIds);
		}

		// 查询日期
		if (json.get("beginDate") != null && StringUtils.isNotBlank(json.get("beginDate").toString())) {
			paramMap.put("beginDate", json.get("beginDate").toString());
		}

		if (json.get("endDate") != null && StringUtils.isNotBlank(json.get("endDate").toString())) {
			paramMap.put("endDate", json.get("endDate").toString() + " 23:59:59");
		}

		Integer totalCount = usedCountMapper.selectUsedCountByMap(paramMap);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("usedCountList", null);
			return json;
		}
		int pageNo = 1;
		int pageSize = 10;
		if (StringUtils.isNotBlank(paramMap.get("pageNo"))) {
			pageNo = Integer.valueOf(paramMap.get("pageNo").toString());
		}
		if (StringUtils.isNotBlank(paramMap.get("pageSize"))) {
			pageSize = Integer.valueOf(paramMap.get("pageSize").toString());
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		paramMap.put("startIndex", page.getStartIndex());

		List<UsedCountInfo> list = usedCountMapper.selectUsedCountListByMap(paramMap);
		for (UsedCountInfo usedCount : list) {
			// 渠道名称
			Channel channel = channelMapper.selectByPrimaryKey(usedCount.getChannelId());
			usedCount.setChannelName(channel == null ? null : channel.getName());

			// 检测项目名称
			CheckItem checkItem = checkItemMapper.selectByPrimaryKey(usedCount.getCheckItemId());
			usedCount.setCheckItemName(checkItem == null ? null : checkItem.getName());

		}
		json.put("page", page);
		json.put("usedCountList", list);
		return json;
	}

	public JSONObject getReport(ReportInfo reportInfo, Map<Long, String> roomMap, Map<Long, String> ysMap,
			Map<Long, String> sbMap, Map<Long, String> acMap, Map<Integer, String> syMap, Map<Integer, String> illMap) {

		JSONObject json = new JSONObject();
		json.put("id", reportInfo.getId());
		json.put("reportName", reportInfo.getReportName());// 报告名称
		json.put("sysReportCode", reportInfo.getSysReportCode());// 系统报告编号
		json.put("auditAccountId", reportInfo.getAuditAccountId());// 审核员id

		// 机构信息
		json.put("hospitalId", reportInfo.getHospitalId());// 机构id
		Hospital hospital = hospitalMapper.selectByPrimaryKey(reportInfo.getHospitalId());
		json.put("hospitalName", hospital == null ? null : hospital.getHospitalName());// 机构名称
		json.put("hospitalRoomId", reportInfo.getHospitalRoomId());// 送检科室id
		json.put("hospitalDoctorId", reportInfo.getHospitalDoctorId());// 送检医生id
		json.put("checkDeviceId", reportInfo.getCheckDeviceId());// 检测设备id
		json.put("checkAccountId", reportInfo.getCheckAccountId());// 检测设备id
		// 基础信息
		json.put("customerPhone", reportInfo.getCustomerPhone());// 用户手机号
		json.put("samplePhone", reportInfo.getSamplePhone());// 采样者手机号
		json.put("sampleCode", reportInfo.getSampleCode());// 采样者编号
		json.put("sampleName", reportInfo.getSampleName());// 采样者姓名
		json.put("sampleSex", reportInfo.getSampleSex() == null || reportInfo.getSampleSex() == 1 ? "男" : "女");// 采样者性别
		json.put("sampleAge", reportInfo.getSampleAge());// 采样者年龄
		json.put("sampleHeight", reportInfo.getSampleHeight() == null ? "0.00" : reportInfo.getSampleHeight() / 100);// 采样者身高
		json.put("sampleWeight", reportInfo.getSampleWeight());// 采样者体重

		json.put("submitTime", reportInfo.getSubmitTime() == null ? null
				: DateUtil.formatDate(reportInfo.getSubmitTime(), "yyyy-MM-dd"));// 采样时间
		json.put("checkTime", reportInfo.getCheckTime() == null ? null
				: DateUtil.formatDate(reportInfo.getCheckTime(), "yyyy-MM-dd"));// 检测时间
		json.put("scanTime", reportInfo.getHospitalScanTime() == null ? null
				: DateUtil.formatDate(reportInfo.getHospitalScanTime(), "yyyy-MM-dd"));// 扫描
		json.put("auditTime", reportInfo.getAuditTime() == null ? null
				: DateUtil.formatDate(reportInfo.getAuditTime(), "yyyy-MM-dd"));// 审核时间

		json.put("ksName",
				reportInfo.getHospitalRoomId() == null || !roomMap.containsKey(reportInfo.getHospitalRoomId()) ? null
						: roomMap.get(reportInfo.getHospitalRoomId()));

		json.put("ysName",
				reportInfo.getHospitalDoctorId() == null || !ysMap.containsKey(reportInfo.getHospitalDoctorId()) ? null
						: ysMap.get(reportInfo.getHospitalDoctorId()));

		json.put("shName",
				reportInfo.getAuditAccountId() == null || !acMap.containsKey(reportInfo.getAuditAccountId()) ? null
						: acMap.get(reportInfo.getAuditAccountId()));

		json.put("jcName",
				reportInfo.getCheckAccountId() == null || !acMap.containsKey(reportInfo.getCheckAccountId()) ? null
						: acMap.get(reportInfo.getCheckAccountId()));

		json.put("sbName",
				reportInfo.getCheckDeviceId() == null || !sbMap.containsKey(reportInfo.getCheckDeviceId().longValue())
						? null
						: sbMap.get(reportInfo.getCheckDeviceId().longValue()));

		// 检测项目及底物
		json.put("checkItemId", reportInfo.getCheckItemId());// 检测项目id
		CheckItem checkItem = checkItemMapper.selectByPrimaryKey(reportInfo.getCheckItemId());
		json.put("checkItemName", checkItem == null ? null : checkItem.getName());// 项目名称
		Substrate substrate = substrateMapper.selectByPrimaryKey(reportInfo.getCheckSubstrateId());
		json.put("checkSubstrateId", reportInfo.getCheckSubstrateId());// 检测项目底物id
		json.put("checkSubstrateName", substrate == null ? null : substrate.getName());// 底物名称
		json.put("checkSubstrateDosage", reportInfo.getCheckSubstrateDosage());// 检测项目底物剂量

		json.put("reportStatus", reportInfo.getReportStatus());

		// 采样疾病信息
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("reportInfoId", reportInfo.getId());
		List<ReportIllness> illnessList = reportIllnessMapper.selectListByMap(map);
		List<ReportSymptom> symptomList = reportSymptomMapper.selectListByMap(map);

		String syStr = "";
		for (ReportSymptom var : symptomList) {
			syStr += !syMap.containsKey(var.getSymptomId()) ? "无" : syMap.get(var.getSymptomId());
			syStr += "【";
			if (var.getSymptomDegree() == 0) {
				syStr += "无";
			} else if (var.getSymptomDegree() == 1) {
				syStr += "轻度";
			} else if (var.getSymptomDegree() == 2) {
				syStr += "中度";
			} else if (var.getSymptomDegree() == 3) {
				syStr += "重度";
			}
			syStr += "】、";
		}
		json.put("symptomList", syStr);// 症状列表

		String syStrN = "";
		for (ReportIllness var : illnessList) {
			syStrN += !illMap.containsKey(var.getIllnessId()) ? "无" : illMap.get(var.getIllnessId());
			syStrN += "【";
			if (var.getIllnessDegree() == 0) {
				syStrN += "无";
			} else if (var.getIllnessDegree() == 1) {
				syStrN += "轻度";
			} else if (var.getIllnessDegree() == 2) {
				syStrN += "中度";
			} else if (var.getIllnessDegree() == 3) {
				syStrN += "重度";
			}
			syStrN += "】、";
		}
		json.put("illnessList", syStrN);// 疾病列表

		json.put("takeAntibiotics",
				reportInfo.getTakeAntibiotics() == null || reportInfo.getTakeAntibiotics() == 0 ? "否" : "是");// 近一个月是否服用抗生素
																												// 0:否
																												// 1:是
		json.put("antibioticsName",
				StringUtils.isBlank(reportInfo.getAntibioticsName()) ? "No" : reportInfo.getAntibioticsName());// 所服用抗生素的药品名称

		json.put("helicobacterPyloriCheck", reportInfo.getHelicobacterPyloriCheck());// 幽门螺旋杆菌检测 0:未检测 1:阳性 2:阴性

		json.put("hpCheckResult",
				StringUtils.isBlank(reportInfo.getHpCheckResult()) ? "No" : reportInfo.getHpCheckResult());// 幽门螺旋杆菌检测结果

		json.put("gastroscopeEnteroscopyCheck", reportInfo.getGastroscopeEnteroscopyCheck());// 胃镜/肠镜检测 0:未检测 1:胃镜 //
																								// 2:肠镜
		json.put("geCheckResult",
				StringUtils.isBlank(reportInfo.getGeCheckResult()) ? "No" : reportInfo.getGeCheckResult());// 胃镜/肠镜检测结果

		json.put("foodMedicineAllergy",
				reportInfo.getFoodMedicineAllergy() == null || reportInfo.getFoodMedicineAllergy() == 0 ? "否" : "是");// 食物/药物过敏
																														// 0:否
																														// 1:是
		json.put("allergyFood", StringUtils.isBlank(reportInfo.getAllergyFood()) ? "No" : reportInfo.getAllergyFood());// 过敏物品名称

		json.put("gasCheckResult", reportInfo.getGasCheckResult());// 气体检测结果描述
		json.put("reportResult", reportInfo.getReportResult());// 报告结果标识
		json.put("reportResultDescription", reportInfo.getReportResultDescription());// 报告结果描述
		json.put("reportDataAnalysis", reportInfo.getReportDataAnalysis());// 数据分析
		json.put("interventionSuggestion", reportInfo.getInterventionSuggestion());// 数据分析

		// 困扰健康的问题
		json.put("besetHealthProblem", reportInfo.getBesetHealthProblem());
		json.put("adviceMsg", reportInfo.getAdviceMsg());
		return json;
	}

	public HSSFWorkbook exportReportCountList(String reportInfoQuery, Account loginAccount, Object apiResult) {

		Map<String, Object> paramMap = null;
		if (StringUtils.isNotBlank(reportInfoQuery)) {
			paramMap = jsonToMap(reportInfoQuery);
		} else {
			paramMap = new HashMap<String, Object>();
		}
		paramMap.put("reportStatus", ReportStatus.RESULT_CREATE.getValue());

		List<Long> hospitalIds = customerManageService.getUserHostList(loginAccount);

		// 这里做权限判定
		if (hospitalIds == null) {
			// 主账号
		} else if (hospitalIds.size() == 0) {
			// 无权限
			hospitalIds.add(0L);
			paramMap.put("hospitalIdList", hospitalIds);
		} else {
			// 非主账号，有权限
			paramMap.put("hospitalIdList", hospitalIds);
		}

		String keyword = "";
		Object obj = paramMap.get("keyword");
		if (StringUtils.isNotBlank(obj)) {
			keyword = obj.toString().replaceAll("\\s*", "");
			paramMap.put("keyword", "%" + keyword + "%");
		} else {
			paramMap.put("keyword", null);
		}
		// 判断是否有illnessId和illnessDegree这两个参数查询
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(paramMap.get("illnessId"))) {
			map.put("illnessId", paramMap.get("illnessId"));
		}
		if (StringUtils.isNotBlank(paramMap.get("illnessDegree"))) {
			map.put("illnessDegree", paramMap.get("illnessDegree"));
		}
		if (map.size() > 0) {
			List<ReportIllness> illnessList = reportIllnessMapper.selectListByMap(map);
			if (illnessList != null && illnessList.size() > 0) {
				List<Long> reportInfoIdList = new ArrayList<Long>();
				for (ReportIllness reportIllness : illnessList) {
					reportInfoIdList.add(reportIllness.getReportInfoId());
				}
				map.put("reportInfoIdList", reportInfoIdList);
			}
		}
		paramMap.put("order_by", "audit_time");
		List<ReportInfo> list = reportInfoMapper.selectListByMap(paramMap);

		Map<String, Object> queryMap = new HashMap<String, Object>();
		List<HospitalRoom> roomList = hospitalRoomMapper.selectListByMap(queryMap);
		Map<Long, String> roomMap = new HashMap<Long, String>();
		for (HospitalRoom hospitalRoom : roomList) {
			roomMap.put(hospitalRoom.getId(), hospitalRoom.getRoomName());
		}

		Map<Long, String> ysMap = new HashMap<Long, String>();
		List<HospitalDoctor> doctorList = hospitalDoctorMapper.selectListByMap(queryMap);
		for (HospitalDoctor hospitalDoctor : doctorList) {
			ysMap.put(hospitalDoctor.getId(), hospitalDoctor.getDoctorName());
		}

		Map<Long, String> sbMap = new HashMap<Long, String>();
		List<CheckDevice> deviceList = checkDeviceMapper.selectListByMap(queryMap);
		for (CheckDevice checkDevice : deviceList) {
			sbMap.put(checkDevice.getId().longValue(), checkDevice.getCode());
		}

		Map<Long, String> acMap = new HashMap<Long, String>();
		List<Account> accountList = accountMapper.selectListByMap(queryMap);
		for (Account accountDo : accountList) {
			acMap.put(accountDo.getId().longValue(), accountDo.getName());
		}

		Map<Integer, String> syMap = new HashMap<Integer, String>();
		List<Symptom> symptomList = symptomMapper.selectListByMap(queryMap);
		for (Symptom symptom : symptomList) {
			syMap.put(symptom.getId(), symptom.getName());
		}

		Map<Integer, String> illMap = new HashMap<Integer, String>();
		List<Illness> illnessList = illnessMapper.selectListByMap(map);
		for (Illness illness : illnessList) {
			illMap.put(illness.getId(), illness.getName());
		}

		List<ReportCountExcel> dataList = new ArrayList<ReportCountExcel>();
		for (ReportInfo reportInfo : list) {
			JSONObject report = getReport(reportInfo, roomMap, ysMap, sbMap, acMap, syMap, illMap);
			ReportCountExcel jb = (ReportCountExcel) JSONObject.toJavaObject(report, ReportCountExcel.class);
			dataList.add(jb);
		}

		// 定义excel列名称字段名
		String[] keyFieldsNew = { "sysReportCode", "hospitalName", "sampleCode", "checkItemName", "checkSubstrateName",
				"samplePhone", "sampleName", "sampleSex", "sampleAge", "sampleWeight", "sampleHeight", "submitTime",
				"scanTime", "checkTime", "auditTime", "sbName", "jcName", "shName", "ksName", "ysName",
				"gasCheckResult", "symptomList", "illnessList", "besetHealthProblem", "antibioticsName",
				"hpCheckResult", "geCheckResult", "allergyFood" };

		String[] valueFieldsNew = { "报告编号", "门店", "渠道自有编号", "检测项目", "底物名称", "电话", "患者姓名", "性别", "年龄岁", "体重kg", "身高m",
				"分析样本创建日期", "样本发回日期", "样本收入日期", "检测完成日期", "设备编号", "检测", "审核", "科室", "医生", "结果", "检测前症状和体征", "合并疾病",
				"困扰健康的问题", "服用过抗生素", "幽门螺旋杆菌", "肠镜检查", "药物过敏" };

		// 生成Excel文件
		HSSFWorkbook book = ExcelUtil.createReportCountExcel("检测报告统计", keyFieldsNew, valueFieldsNew, dataList);
		return book;
	}

	public HSSFWorkbook exportUsedCountList(String usedCountQuery, Account loginAccount, Object apiResult) {
		JSONObject json = new JSONObject();
		Map<String, Object> paramMap = null;
		if (StringUtils.isNotBlank(usedCountQuery)) {
			paramMap = jsonToMap(usedCountQuery);
		} else {
			paramMap = new HashMap<String, Object>();
		}

		List<Long> hospitalIds = customerManageService.getUserHostList(loginAccount);

		// 这里做权限判定
		if (hospitalIds == null) {
			// 主账号
		} else if (hospitalIds.size() == 0) {
			// 无权限
			hospitalIds.add(0L);
			paramMap.put("hospitalIdList", hospitalIds);
		} else {
			// 非主账号，有权限
			paramMap.put("hospitalIdList", hospitalIds);
		}

		// 查询日期
		if (json.get("beginDate") != null && StringUtils.isNotBlank(json.get("beginDate").toString())) {
			paramMap.put("beginDate", json.get("beginDate").toString());
		}

		if (json.get("endDate") != null && StringUtils.isNotBlank(json.get("endDate").toString())) {
			paramMap.put("endDate", json.get("endDate").toString());
		}

		Integer totalCount = usedCountMapper.selectUsedCountByMap(paramMap);
		if (totalCount == null || totalCount == 0) {
			// 定义excel列名称字段名
			String[] keyFields = { "queryDate", "areaCodeName", "channelName", "queryDate", "hospitalName", "num",
					"checkItemName" };
			String[] valueFields = { "日期", "地区", "渠道", "门店", "用量", "项目名称" };
			// 生成Excel文件
			HSSFWorkbook book = ExcelUtil.createUsedCountExcel("用量统计报表", keyFields, valueFields,
					new ArrayList<UsedCountInfo>());
			return book;
		}
		int pageNo = 1;
		int pageSize = 10000;
		if (StringUtils.isNotBlank(paramMap.get("pageNo"))) {
			pageNo = Integer.valueOf(paramMap.get("pageNo").toString());
		}
		if (StringUtils.isNotBlank(paramMap.get("pageSize"))) {
			pageSize = Integer.valueOf(paramMap.get("pageSize").toString());
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		paramMap.put("startIndex", page.getStartIndex());

		List<UsedCountInfo> list = usedCountMapper.selectUsedCountListByMap(paramMap);

		for (UsedCountInfo usedCount : list) {
			// 渠道名称
			Channel channel = channelMapper.selectByPrimaryKey(usedCount.getChannelId());
			usedCount.setChannelName(channel == null ? null : channel.getName());

			// 检测项目名称
			CheckItem checkItem = checkItemMapper.selectByPrimaryKey(usedCount.getCheckItemId());
			usedCount.setCheckItemName(checkItem == null ? null : checkItem.getName());
		}
		// 定义excel列名称字段名
		String[] keyFields = { "queryDate", "areaCodeName", "channelName", "queryDate", "hospitalName", "num",
				"checkItemName" };
		String[] valueFields = { "日期", "地区", "渠道", "门店", "用量", "项目名称" };
		// 生成Excel文件
		HSSFWorkbook book = ExcelUtil.createUsedCountExcel("用量统计报表", keyFields, valueFields, list);
		return book;
	}

	public JSONObject previewReport(Long reportInfoId, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(reportInfoId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数reportInfoId为空");
			return null;
		}
		List<String> urlList = new ArrayList<String>();
		ReportInfo reportInfo = reportInfoMapper.selectByPrimaryKey(reportInfoId);
		if (reportInfo != null) {
			String reportPictureIds = reportInfo.getReportPictureIds();
			if (reportPictureIds != null) {
				String[] arr = reportPictureIds.split("\\,");
				if (arr != null && arr.length > 0) {
					for (String id : arr) {
						SysFile sysFile = sysFileMapper.selectByPrimaryKey(Long.valueOf(id));
						if (sysFile != null) {
							urlList.add(sysFile.getUrl());
						}
					}
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("urlList", urlList);
		return json;
	}
}
