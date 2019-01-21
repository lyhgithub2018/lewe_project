package com.lewe.serviceImpl.check;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.check.CheckDevice;
import com.lewe.bean.check.CheckItem;
import com.lewe.bean.check.CheckItemSubstrate;
import com.lewe.bean.check.GasBagDefault;
import com.lewe.bean.check.Substrate;
import com.lewe.bean.customer.query.SampleInfoQuery;
import com.lewe.bean.hospital.Hospital;
import com.lewe.bean.report.ReportCheckData;
import com.lewe.bean.report.ReportIllness;
import com.lewe.bean.report.ReportInfo;
import com.lewe.bean.report.ReportSymptom;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.ShowField;
import com.lewe.dao.check.CheckDeviceMapper;
import com.lewe.dao.check.CheckItemMapper;
import com.lewe.dao.check.CheckItemSubstrateMapper;
import com.lewe.dao.check.GasBagDefaultMapper;
import com.lewe.dao.check.SubstrateMapper;
import com.lewe.dao.hospital.HospitalMapper;
import com.lewe.dao.report.ReportCheckDataMapper;
import com.lewe.dao.report.ReportIllnessMapper;
import com.lewe.dao.report.ReportInfoMapper;
import com.lewe.dao.report.ReportSymptomMapper;
import com.lewe.dao.sys.AccountMapper;
import com.lewe.dao.sys.ShowFieldMapper;
import com.lewe.service.check.ICheckManageService;
import com.lewe.service.customer.ICustomerManageService;
import com.lewe.service.sys.ISysLogService;
import com.lewe.serviceImpl.check.bo.AuditInfoBo;
import com.lewe.serviceImpl.check.bo.CheckInfoBo;
import com.lewe.serviceImpl.check.bo.CheckItemBo;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.DateUtil;
import com.lewe.util.common.JedisUtil;
import com.lewe.util.common.Page;
import com.lewe.util.common.StringUtils;
import com.lewe.util.common.aliyun.SMSUtil;
import com.lewe.util.common.constants.ReportStatus;
import com.lewe.util.common.mei.nian.MeiNianReportUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("checkManageService")
public class CheckManageServiceImpl implements ICheckManageService {

	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private CheckItemMapper checkItemMapper;
	@Autowired
	private CheckDeviceMapper checkDeviceMapper;
	@Autowired
	private ReportInfoMapper reportInfoMapper;
	@Autowired
	private ReportIllnessMapper reportIllnessMapper;
	@Autowired
	private ReportSymptomMapper reportSymptomMapper;
	@Autowired
	private HospitalMapper hospitalMapper;
	@Autowired
	private GasBagDefaultMapper gasBagDefaultMapper;
	@Autowired
	private ReportCheckDataMapper reportCheckDataMapper;
	@Autowired
	private SubstrateMapper substrateMapper;
	@Autowired
	private CheckItemSubstrateMapper checkItemSubstrateMapper;

	@Autowired
	private ICustomerManageService customerManageService;

	public static final Logger logger = LoggerFactory.getLogger(CheckManageServiceImpl.class);

	@Transactional
	public int saveCheckItem(String jsonstr, Account account, Object apiResult) {
		if (jsonstr != null) {
			ApiResult result = (ApiResult) apiResult;
			CheckItemBo checkItemBo = JSONObject.parseObject(jsonstr, CheckItemBo.class);
			if (StringUtils.isBlank(checkItemBo.getName())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请输入项目名称");
				return 0;
			}
			List<CheckItemSubstrate> substrateList = checkItemBo.getSubstrateList();
			if (substrateList == null || substrateList.size() == 0) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请至少选择一组底物与剂量");
				return 0;
			}
			for (CheckItemSubstrate substrate : substrateList) {
				if (StringUtils.isBlank(substrate.getSubstrateId())) {
					result.setCode(BizCode.PARAM_EMPTY);
					result.setMessage("请选择底物");
					return 0;
				}
				if (StringUtils.isBlank(substrate.getSubstrateDosage())) {
					result.setCode(BizCode.PARAM_EMPTY);
					result.setMessage("请选择剂量");
					return 0;
				}
			}
			CheckItem checkItem = new CheckItem();
			checkItem.setName(checkItemBo.getName());
			if (checkItemBo.getId() == null) {// 新增
				checkItem.setCreateTime(new Date());
				checkItem.setCreatorId(account.getId());
				checkItemMapper.insertSelective(checkItem);
				// 保存项目底物
				for (CheckItemSubstrate itemSubstrate : substrateList) {
					itemSubstrate.setId(null);
					itemSubstrate.setCheckItemId(checkItem.getId());
					// 底物名称字段赋值(该字段是冗余字段)
					Substrate substrate = substrateMapper.selectByPrimaryKey(itemSubstrate.getSubstrateId());
					if (substrate != null) {
						itemSubstrate.setSubstrateName(substrate.getName());
					}
					itemSubstrate.setCreatorId(account.getId());
					checkItemSubstrateMapper.insertSelective(itemSubstrate);
				}
			} else {// 修改
				checkItem.setId(checkItemBo.getId());
				checkItem.setUpdateTime(new Date());
				checkItemMapper.updateByPrimaryKeySelective(checkItem);
				checkItemSubstrateMapper.deleteByItemId(checkItem.getId());
				// 更新项目底物
				for (CheckItemSubstrate itemSubstrate : substrateList) {
					itemSubstrate.setId(null);
					itemSubstrate.setCheckItemId(checkItem.getId());
					Substrate substrate = substrateMapper.selectByPrimaryKey(itemSubstrate.getSubstrateId());
					if (substrate != null) {
						itemSubstrate.setSubstrateName(substrate.getName());
					}
					checkItemSubstrateMapper.insertSelective(itemSubstrate);
				}
			}
		}
		return 1;
	}

	@Transactional
	public int modifyAdvice(String jsonstr, Account account, Object apiResult) {
		if (jsonstr != null) {
			ApiResult result = (ApiResult) apiResult;
			CheckItemBo checkItemBo = JSONObject.parseObject(jsonstr, CheckItemBo.class);
			if (StringUtils.isBlank(checkItemBo.getName())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请输入建议");
				return 0;
			}

			ReportInfo rf = new ReportInfo();
			rf.setId(checkItemBo.getId().longValue());
			rf.setAdviceMsg(checkItemBo.getName());
			reportInfoMapper.updateByPrimaryKeySelective(rf);
		}
		return 1;
	}

	@Transactional
	public int delCheckItem(String itemId, Account account, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(itemId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的项目");
			return 0;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("checkItemId", itemId);
		Integer count = reportInfoMapper.selectCountByMap(paramMap);
		if (count != null && count > 0) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("该检测项目不可删除");
			return 0;
		}
		CheckItem record = new CheckItem();
		record.setId(Integer.valueOf(itemId));
		record.setUpdateTime(new Date());
		record.setIsDel((byte) 1);
		checkItemMapper.updateByPrimaryKeySelective(record);
		return 1;
	}

	public JSONObject getCheckItemList(String keyword, Integer pageNo, Integer pageSize, Account account,
			Object apiResult) {
		JSONObject json = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isDel", 0);
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", "");
			map.put("keyword", "%" + keyword + "%");
		}
		Integer totalCount = checkItemMapper.selectCountWithSubstrate(map);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("checkItemList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		List<CheckItem> list = checkItemMapper.selectListWithSubstrate(map);
		for (CheckItem checkItem : list) {
			JSONObject itemInfo = new JSONObject();
			itemInfo.put("id", checkItem.getId());
			itemInfo.put("name", checkItem.getName());
			List<CheckItemSubstrate> substrateList = checkItemSubstrateMapper.selectListByItemId(checkItem.getId());
			itemInfo.put("substrateList", substrateList);// 底物列表
			jsonList.add(itemInfo);
		}
		json.put("page", page);
		json.put("checkItemList", jsonList);
		return json;
	}

	public JSONObject checkSampleList(String sampleInfoQuery, Account loginAccount, Object apiResult) {
		SampleInfoQuery query = null;
		JSONObject json = new JSONObject();
		if (StringUtils.isNotBlank(sampleInfoQuery)) {
			query = JSONObject.parseObject(sampleInfoQuery, SampleInfoQuery.class);
		} else {
			query = new SampleInfoQuery();
		}

		List<Long> hosIdList = customerManageService.getUserHostList(loginAccount);

		// 这里做权限判定
		if (hosIdList == null) {
			// 主账号
		} else if (hosIdList.size() == 0) {
			// 无权限
			hosIdList.add(0L);
			query.setHospitalIdList(hosIdList);
		} else {
			// 非主账号，有权限
			query.setHospitalIdList(hosIdList);
		}

		String keyword = query.getKeyword();
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", "");
			query.setKeyword("%" + keyword + "%");
		} else {
			query.setKeyword(null);
		}
		if (StringUtils.isBlank(query.getBeginDate())) {
			query.setBeginDate(null);
		}
		if (StringUtils.isBlank(query.getEndDate())) {
			query.setEndDate(null);
		}
		logger.error(JSON.toJSONString(query));

		Integer totalCount = reportInfoMapper.selectCountBySampleInfoQuery(query);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("sampleInfoList", null);
			return json;
		}
		Page page = new Page(query.getPageNo(), query.getPageSize(), totalCount);
		query.setStartIndex(page.getStartIndex());
		List<ReportInfo> list = reportInfoMapper.selectListBySampleInfoQuery(query);
		List<JSONObject> sampleInfoList = new ArrayList<JSONObject>();
		for (ReportInfo reportInfo : list) {
			JSONObject sampleInfo = new JSONObject();
			sampleInfo.put("id", reportInfo.getId());
			sampleInfo.put("customerPhone", reportInfo.getCustomerPhone());// 用户手机号
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
			sampleInfo.put("submitTime", reportInfo.getSubmitTime() == null ? null
					: DateUtil.formatDate(reportInfo.getSubmitTime(), "yyyy-MM-dd"));// 采样提交时间
			sampleInfo.put("checkTime", reportInfo.getCheckTime() == null ? null
					: DateUtil.formatDate(reportInfo.getCheckTime(), "yyyy-MM-dd"));// 检测时间
			sampleInfo.put("hospitalScanTime", reportInfo.getHospitalScanTime() == null ? null
					: DateUtil.formatDate(reportInfo.getHospitalScanTime(), "yyyy-MM-dd"));// 门店扫描日期
			sampleInfo.put("checkStatus", reportInfo.getCheckStatus());// 检测状态 0：待检测 1：已检测
			sampleInfo.put("reportStatus", reportInfo.getReportStatus());// 报告状态 1：用户绑定 2：门店扫码 3：邮寄单号 4：乐为扫码 5：结果生成
			Account account = accountMapper.selectByPrimaryKey(reportInfo.getCheckAccountId());
			sampleInfo.put("checkerName", account == null ? null : account.getName());// 检测员

			Hospital hospital = hospitalMapper.selectByPrimaryKey(reportInfo.getHospitalId());
			sampleInfo.put("hospitalName", hospital == null ? null : hospital.getHospitalName());// 送检机构名称
			sampleInfoList.add(sampleInfo);
		}
		json.put("page", page);
		json.put("sampleInfoList", sampleInfoList);
		return json;
	}

	public JSONObject checkInfoDetail(Long id, String sampleCode, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (id == null && StringUtils.isBlank(sampleCode)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数错误");
			return null;
		}
		JSONObject json = new JSONObject();
		ReportInfo reportInfo = null;
		if (id != null) {
			reportInfo = reportInfoMapper.selectByPrimaryKey(id);
		} else {
			reportInfo = reportInfoMapper.selectBySampleCode(sampleCode);
			if (reportInfo == null) {
				result.setCode(BizCode.DATA_NOT_FOUND);
				result.setMessage("未查询到编号【" + sampleCode + "】对应的客户信息");
				return null;
			}
		}
		if (reportInfo != null) {
			json.put("id", reportInfo.getId());
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
			json.put("sampleSex", reportInfo.getSampleSex());// 采样者性别
			json.put("sampleAge", reportInfo.getSampleAge());// 采样者年龄
			json.put("sampleHeight", reportInfo.getSampleHeight());// 采样者身高
			json.put("sampleWeight", reportInfo.getSampleWeight());// 采样者体重
			json.put("submitTime", reportInfo.getSubmitTime() == null ? null
					: DateUtil.formatDate(reportInfo.getSubmitTime(), "yyyy-MM-dd"));// 采样时间
			json.put("checkTime", reportInfo.getCheckTime() == null ? null
					: DateUtil.formatDate(reportInfo.getCheckTime(), "yyyy-MM-dd"));// 检测时间
			// 检测项目及底物
			json.put("checkItemId", reportInfo.getCheckItemId());// 检测项目id
			CheckItem checkItem = checkItemMapper.selectByPrimaryKey(reportInfo.getCheckItemId());
			json.put("checkItemName", checkItem == null ? null : checkItem.getName());// 项目名称
			Substrate substrate = substrateMapper.selectByPrimaryKey(reportInfo.getCheckSubstrateId());
			json.put("checkSubstrateId", reportInfo.getCheckSubstrateId());// 检测项目底物id
			json.put("checkSubstrateName", substrate == null ? null : substrate.getName());// 底物名称
			json.put("checkSubstrateDosage", reportInfo.getCheckSubstrateDosage());// 检测项目底物剂量
			// 底物和剂量(一个检测项目会有多组底物和剂量)
			List<CheckItemSubstrate> substrateList = checkItemSubstrateMapper
					.selectListByItemId(reportInfo.getCheckItemId());
			json.put("substrateList", substrateList);
			json.put("reportStatus", reportInfo.getReportStatus());
			// 采样疾病信息
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("reportInfoId", reportInfo.getId());
			List<ReportIllness> illnessList = reportIllnessMapper.selectListByMap(map);
			List<ReportSymptom> symptomList = reportSymptomMapper.selectListByMap(map);
			json.put("illnessList", illnessList);// 疾病列表
			json.put("symptomList", symptomList);// 症状列表
			json.put("takeAntibiotics", reportInfo.getTakeAntibiotics());// 近一个月是否服用抗生素 0:否 1:是
			json.put("antibioticsName", reportInfo.getAntibioticsName());// 所服用抗生素的药品名称
			json.put("helicobacterPyloriCheck", reportInfo.getHelicobacterPyloriCheck());// 幽门螺旋杆菌检测 0:未检测 1:阳性 2:阴性
			json.put("hpCheckResult", reportInfo.getHpCheckResult());// 幽门螺旋杆菌检测结果
			json.put("gastroscopeEnteroscopyCheck", reportInfo.getGastroscopeEnteroscopyCheck());// 胃镜/肠镜检测 0:未检测 1:胃镜
																									// 2:肠镜
			json.put("geCheckResult", reportInfo.getGeCheckResult());// 胃镜/肠镜检测结果
			json.put("foodMedicineAllergy", reportInfo.getFoodMedicineAllergy());// 食物/药物过敏 0:否 1:是
			json.put("allergyFood", reportInfo.getAllergyFood());// 过敏物品名称
			// 检测数据
			List<ReportCheckData> checkDataList = reportCheckDataMapper.selectListByMap(map);

			if (checkDataList == null || checkDataList.size() == 0) {
				checkDataList = new ArrayList<ReportCheckData>();
				// 若未查询到检测数据,则取出默认的气袋数据规格
				List<GasBagDefault> gasBagDefault = gasBagDefaultMapper.selectAllList();
				for (GasBagDefault gasBag : gasBagDefault) {

					if (reportInfo.getSampleAge() != null && reportInfo.getSampleAge().intValue() <= 18
							&& gasBag.getType().intValue() == 1) {
						ReportCheckData checkData = new ReportCheckData();
						checkData.setCheckTime(gasBag.getCheckTime());
						checkData.setTimeSpace(gasBag.getTimeSpace());
						checkDataList.add(checkData);
					}

					if (reportInfo.getSampleAge() != null && reportInfo.getSampleAge().intValue() > 18
							&& gasBag.getType().intValue() == 0) {
						ReportCheckData checkData = new ReportCheckData();
						checkData.setCheckTime(gasBag.getCheckTime());
						checkData.setTimeSpace(gasBag.getTimeSpace());
						checkDataList.add(checkData);
					}
				}
			}

			List<JSONObject> jsonList = new ArrayList<JSONObject>();
			for (ReportCheckData reportCheckData : checkDataList) {
				JSONObject checkData = new JSONObject();
				checkData.put("timeSpace", reportCheckData.getTimeSpace());// 时间间隔
				checkData.put("checkTime", reportCheckData.getCheckTime());// 检测时间
				checkData.put("h2Concentration", reportCheckData.getH2Concentration());// 氢气浓度
				checkData.put("ch4Concentration", reportCheckData.getCh4Concentration());// 甲烷浓度
				checkData.put("co2Concentration", reportCheckData.getCo2Concentration());// 二氧化碳浓度
				jsonList.add(checkData);
			}
			json.put("checkDataList", jsonList);// 检测数据
			// 困扰健康的问题
			json.put("besetHealthProblem", reportInfo.getBesetHealthProblem());
			json.put("reportNeedAduit", hospital == null ? null : hospital.getReportNeedAduit());// 是否需要审核 0:否 1:是
		}
		return json;
	}

	@Autowired
	private ISysLogService sysLogService;

	@Transactional
	public int submitCheckInfo(String checkInfo, Account loginAccount, Object apiResult) {
		if (StringUtils.isNotBlank(checkInfo)) {
			CheckInfoBo checkInfoBo = JSONObject.parseObject(checkInfo, CheckInfoBo.class);
			ApiResult result = (ApiResult) apiResult;
			if (checkInfoBo.getId() == null) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("参数id不可为空");
				return 0;
			}
			if (checkInfoBo.getHospitalRoomId() == null) {
				// result.setCode(BizCode.PARAM_EMPTY);
				// result.setMessage("请选择送检科室");
				// return 0;
				checkInfoBo.setHospitalRoomId(Long.parseLong("0"));
			}
			if (checkInfoBo.getHospitalDoctorId() == null) {
				// result.setCode(BizCode.PARAM_EMPTY);
				// result.setMessage("请选择送检医生");
				// return 0;
				checkInfoBo.setHospitalDoctorId(Long.parseLong("0"));
			}
			if (checkInfoBo.getCheckDeviceId() == null) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择检测设备");
				return 0;
			}
			if (checkInfoBo.getCheckAccountId() == null) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择检测员");
				return 0;
			}
			if (StringUtils.isBlank(checkInfoBo.getSamplePhone())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者手机号");
				return 0;
			}
			if (StringUtils.isBlank(checkInfoBo.getSampleCode())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者编号");
				return 0;
			}
			if (StringUtils.isBlank(checkInfoBo.getSampleName())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者姓名");
				return 0;
			}
			if (StringUtils.isBlank(checkInfoBo.getSampleSex())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者性别");
				return 0;
			}
			if (StringUtils.isBlank(checkInfoBo.getSampleAge())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者年龄");
				return 0;
			}
			if (StringUtils.isBlank(checkInfoBo.getCheckSubstrateId())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择底物");
				return 0;
			}
			if (StringUtils.isBlank(checkInfoBo.getCheckSubstrateDosage())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择底物剂量");
				return 0;
			}
			if (StringUtils.isBlank(checkInfoBo.getCheckItemId())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择检测项目");
				return 0;
			}

			if (checkInfoBo.getHospitalId() == null) {
				// 若未选择送检门店,则后台默认当前账号所属的门店id
				checkInfoBo.setHospitalId(loginAccount.getHospitalId());
			}

			// 更新检测报告信息主表
			ReportInfo reportInfo = new ReportInfo();
			BeanUtils.copyProperties(checkInfoBo, reportInfo);

			// 若未选择送检门店,则后台默认当前账号所属的门店id
			if (loginAccount.getHospitalGroupId() != null) {
				reportInfo.setHospitalGroupId(loginAccount.getHospitalGroupId());
			}

			// 系统报告编号:门店编号+设备编号+日期编号+流水编号
			String sysReportCode = "";
			JedisUtil redis = JedisUtil.getInstance();
			String key = "lewe_checkReport_flowCode:" + reportInfo.getId();
			String flowCode = redis.get(key);// 流水编号
			Hospital hospital = hospitalMapper.selectByPrimaryKey(reportInfo.getHospitalId());
			CheckDevice checkDevice = checkDeviceMapper.selectByPrimaryKey(reportInfo.getCheckDeviceId());
			if (hospital != null && checkDevice != null) {
				if (flowCode != null) {
					flowCode = (Integer.valueOf(flowCode) + 1) + "";
					redis.set(key, flowCode);
					if (flowCode.length() == 1) {
						flowCode = "000" + flowCode;
					} else if (flowCode.length() == 2) {
						flowCode = "00" + flowCode;
					} else if (flowCode.length() == 3) {
						flowCode = "0" + flowCode;
					}
				} else {
					// 默认值
					flowCode = "0001";
					redis.set(key, "1");
				}
				sysReportCode = hospital.getHospitalCode() + checkDevice.getCode()
						+ DateUtil.formatDate(new Date(), "yyyyMMdd") + flowCode;
			}
			reportInfo.setSysReportCode(sysReportCode);
			reportInfo.setCheckStatus((byte) 1);// 改成已检测
			reportInfo.setCheckTime(new Date());
			// 报告状态 1:用户绑定 2:门店扫码 3:邮寄单号 4:乐为扫码 5:结果生成
			if (hospital.getReportNeedAduit() == 0) {// 判断如果不需要审核,则报告直接在这个接口生成
				reportInfo.setReportStatus(ReportStatus.RESULT_CREATE.getValue());// 报告状态改成结果生成
				reportInfo.setReportCreateTime(new Date());
				// 发送短信通知用户报告生成
				SMSUtil.sendReportNoticeSMS(reportInfo.getSamplePhone(), reportInfo.getSampleName());
			} else if (hospital.getReportNeedAduit() == 1) {// 需要审核的,则在审核通过操作的接口中生成报告
				reportInfo.setReportStatus(ReportStatus.LEWE_SCAN.getValue());// 报告状态改成乐为扫码
			}
			reportInfoMapper.updateByPrimaryKeySelective(reportInfo);

			// 更新疾病列表
			List<ReportIllness> illnessList = checkInfoBo.getIllnessList();
			reportIllnessMapper.deleteByReportInfoId(reportInfo.getId());
			for (ReportIllness illness : illnessList) {
				illness.setId(null);
				illness.setReportInfoId(reportInfo.getId());
				reportIllnessMapper.insertSelective(illness);
			}
			// 更新症状列表
			List<ReportSymptom> symptomList = checkInfoBo.getSymptomList();
			reportSymptomMapper.deleteByReportInfoId(reportInfo.getId());
			for (ReportSymptom symptom : symptomList) {
				symptom.setId(null);
				symptom.setReportInfoId(reportInfo.getId());
				reportSymptomMapper.insertSelective(symptom);
			}
			// 气袋检测数据
			reportCheckDataMapper.deleteByReportInfoId(reportInfo.getId());
			List<ReportCheckData> checkDataList = checkInfoBo.getCheckDataList();
			int checkPoint = 0;
			for (ReportCheckData reportCheckData : checkDataList) {
				reportCheckData.setId(null);
				reportCheckData.setCheckPoint(checkPoint);
				reportCheckData.setReportInfoId(reportInfo.getId());
				reportCheckData.setCreateTime(new Date());
				reportCheckDataMapper.insertSelective(reportCheckData);
				checkPoint++;
			}

			// 调用呼气检测数据诊断算法得到检测结果
			Byte isHospital = 0;
			if (hospital != null) {
				isHospital = hospital.getIsHospital();
			}
			JSONObject json = getGasCheckResult(checkDataList, isHospital);
			if (json != null) {

				ReportInfo update = new ReportInfo();
				update.setId(reportInfo.getId());
				int reportResult = json.getIntValue("reslut");
				update.setReportResult(reportResult);// 报告结果标识 1：轻度 2：中度 3：重度 4：阳性 5：阴性

				// 气体数据检测结果描述
				if (reportResult == 5) {
					update.setGasCheckResult("菌群平衡，小肠细菌过度生长" + json.getString("reslutDesc"));
				} else {
					update.setGasCheckResult("菌群失衡，小肠细菌过度生长" + json.getString("reslutDesc"));
				}

				// 检后分析文案处理(依据甲方提供的文案)
				Integer sampleAge = reportInfo.getSampleAge();
				String reportResultDescription = "";
				if (sampleAge <= 18) {// 小于18岁时的文案(儿童版)
					if (reportResult == 5) {
						reportResultDescription = "健康的肠道菌群有助于孩子的消化吸收、排毒通便、生长发育以及免疫力。";
					} else if (reportResult == 1) {
						reportResultDescription = "有害菌过多会产生吲哚、胺类、氨、硫化氢等有毒物质，肠粘膜通透性增加，有毒物质进入血液循环，对多系统、多器官造成伤害。";
					} else if (reportResult == 2) {
						reportResultDescription = "肠道菌群长期严重失衡，易形成消化系统溃疡，甚至损害心脏、肾脏等，影响孩子生长发育。";
					} else if (reportResult == 3) {
						reportResultDescription = "肠道菌群长期严重失衡，易形成消化系统溃疡、息肉，甚至损害心脏、肾脏等，影响孩子生长发育、免疫机能、睡眠情绪、学习能力等。";
					} else if (reportResult == 4) {// 如果结果为阳性,则文案取'轻度'的文案
						reportResultDescription = "有害菌过多会产生吲哚、胺类、氨、硫化氢等有毒物质，肠粘膜通透性增加，有毒物质进入血液循环，对多系统、多器官造成伤害。";
					}
				} else {// 大于18岁时的文案(成人版)
					if (reportResult == 5) {
						reportResultDescription = "健康的肠道菌群有助于我们的消化吸收、排毒通便以及增强免疫力。";
					} else if (reportResult == 1) {
						reportResultDescription = "有害菌过多会产生吲哚、胺类、氨、硫化氢等有毒物质，肠粘膜通透性增加，有毒物质进入血液循环，对多系统、多器官造成伤害。";
					} else if (reportResult == 2) {
						reportResultDescription = "肠道菌群长期严重失衡，易形成消化系统溃疡，甚至损害心脏、肾脏等，同时常伴有皮肤暗黄、口臭、腹泻、便秘等。";
					} else if (reportResult == 3) {
						reportResultDescription = "肠道菌群长期严重失衡，易形成消化系统溃疡、息肉，甚至损害心脏、肾脏等，最终可能导致癌症。";
					} else if (reportResult == 4) {// 如果结果为阳性,则文案取'轻度'的文案
						reportResultDescription = "有害菌过多会产生吲哚、胺类、氨、硫化氢等有毒物质，肠粘膜通透性增加，有毒物质进入血液循环，对多系统、多器官造成伤害。";
					}
				}
				update.setReportResultDescription(reportResultDescription);

				// 数据分析文案
				String reportDataAnalysis = "在0min数值超出正常数值引起的原因有平时的吸烟，喝酒，口腔卫生习惯等引起，在30min时消化在胃部，数值呈上升趋势，胃部可能存在hp，建议去做hp检测，在60-90min消化进入小肠段数值继续呈上升趋势，证明小肠段有细菌存在，发生肠道菌群失衡，您的数值已达到重度请仔细阅读我们的干预建议，及时到医院就诊。";
				update.setReportDataAnalysis(reportDataAnalysis);
				reportInfoMapper.updateByPrimaryKeySelective(update);
			}
			String content = "提交了检测信息,姓名:" + reportInfo.getSampleName() + ",检测编号:" + reportInfo.getSampleCode();
			sysLogService.addSysLog(loginAccount, content, new Date());

			// 判断如果是美年端的报告,则需要调用美年的保存信息接口(仅针对不需要审核的报告在这里处理,需要审核的则在审核接口里处理)
			if (hospital != null && hospital.getReportNeedAduit() == 0) {
				if (hospital.getHospitalName().contains("美年")) {
					JSONObject param = new JSONObject();
					param.put("hospitalName", hospital.getHospitalName());// 门店名称
					param.put("hospitalCode", hospital.getHospitalCode());// 门店编码
					param.put("name", checkInfoBo.getSampleName());// 姓名
					Byte sex = checkInfoBo.getSampleSex();
					if (sex == 1) {
						json.put("sex", "M");// 男
					} else if (sex == 2) {
						json.put("sex", "F");// 女
					}
					json.put("vid", checkInfoBo.getSampleCode());// 采样编码
					json.put("itemId", "10160");// 项目编码(固定)
					json.put("itemName", "肠道菌群无创检测");// 项目名称(固定)
					// json.put("npMark", "阳性");//阴阳性标识
					json.put("reportNum", "4");// 报告张数
					MeiNianReportUtil.addCheckInfo(param);
				}
				// 报告名称
				// 美年端为'全肠道菌群无创吹气检查报告',其他为'甲烷氢呼气检查报告解析'
				String reportName = "全肠道菌群无创吹气检查报告";
				if (!hospital.getHospitalName().contains("美年")) {
					reportName = "甲烷氢呼气检查报告解析";
				}
				ReportInfo update = new ReportInfo();
				update.setId(reportInfo.getId());
				update.setReportName(reportName);
				reportInfoMapper.updateByPrimaryKeySelective(update);
			}
		}
		return 1;
	}

	/**
	 * 依据甲方提供的呼气检测标准算法计算出检测结果
	 * 
	 * @param checkDataList
	 * @return
	 */
	private JSONObject getGasCheckResult(List<ReportCheckData> checkDataList, byte isHospital) {
		if (checkDataList == null || checkDataList.size() == 0) {
			return null;
		}
		int reslut = 0;// 结果标识 1:轻度 2:中度 3:重度 4:阳性 5:阴性
		String reslutDesc = "";// 结果描述
		ReportCheckData checkData0 = new ReportCheckData();// 第1代气
		ReportCheckData checkData1 = new ReportCheckData();// 第2代气
		for (ReportCheckData checkData : checkDataList) {
			if (checkData.getCheckPoint() == 0) {
				checkData0 = checkData;
			} else if (checkData.getCheckPoint() == 1) {
				checkData1 = checkData;
			}
		}

		// 分别取出第1,2袋的氢气,甲烷,浓度数据;这次肯定不是 669 了
		Integer H2Con0 = checkData0.getH2Concentration() == null ? 0 : checkData0.getH2Concentration().intValue();
		Integer CH4Con0 = checkData0.getCh4Concentration() == null ? 0 : checkData0.getCh4Concentration().intValue();

		Integer H2Con1 = checkData1.getH2Concentration() == null ? 0 : checkData1.getH2Concentration().intValue();
		Integer CH4Con1 = checkData1.getCh4Concentration() == null ? 0 : checkData1.getCh4Concentration().intValue();

		int H2max = 0;// 氢气浓度最大值
		int CH4max = 0;// 甲烷浓度最大值
		int CO2max = 0;// 二氧化碳浓度最大值
		int addMax = 0;
		for (ReportCheckData checkData : checkDataList) {

			if (checkData.getCheckPoint() == 0) {
				continue;
			} else if (checkData.getCheckPoint() == 1) {
				continue;
			}

			Integer H2Con = checkData.getH2Concentration() == null ? 0 : checkData.getH2Concentration().intValue();
			if (H2Con > H2max) {
				H2max = H2Con;
			}
			Integer CH4Con = checkData.getCh4Concentration() == null ? 0 : checkData.getCh4Concentration().intValue();
			if (CH4Con > CH4max) {
				CH4max = CH4Con;
			}
			Integer CO2Con = checkData.getCo2Concentration() == null ? 0 : checkData.getCo2Concentration().intValue();
			if (CO2Con > CO2max) {
				CO2max = CO2Con;
			}

			if ((CH4Con + H2Con) > addMax) {
				addMax = CH4Con + H2Con;
			}
		}

		int CH4Sub = CH4max - CH4Con1;// CH4浓度差值 -1
		if (CH4Sub < 0) {
			CH4Sub = 0;
		}

		int H2Sub = H2max - H2Con1;// H2浓度差值
		if (H2Sub < 0) {
			H2Sub = 0;
		}

		int CH4_H2Sub = (CH4max + H2max) - (CH4Con1 + H2Con1); // 22
		if (CH4_H2Sub < 0) {
			CH4_H2Sub = 0;
		}

		int CH4_H2Sub_AddMax = addMax - (CH4Con1 + H2Con1); // 22addMax
		if (CH4_H2Sub_AddMax < 0) {
			CH4_H2Sub_AddMax = 0;
		}
		CH4_H2Sub = CH4_H2Sub_AddMax;

		logger.error(
				"CH4Con0=" + CH4Con0 + ";H2Con0=" + H2Con0 + ";" + "CH4Con1=" + CH4Con1 + ";H2Con1=" + H2Con1 + ";");
		logger.error("H2max=" + H2max + ";CH4max=" + CH4max + ";" + "CH4Sub=" + CH4Sub + ";H2Sub=" + H2Sub
				+ ";CH4_H2Sub=" + CH4_H2Sub);

		if (CH4Con0 <= 12 && H2Con0 > 20) {
			if (H2Con1 > 40) {
				reslut = 3;
				reslutDesc = "重度阳性";
				logger.error("CH4Con0 <= 12 && H2Con0 > 20 H2Con1 > 40 reslut=" + reslut + " reslutDesc=" + reslutDesc);
			} else if (H2Con1 > 20 && H2Con1 <= 40) {
				reslut = 2;
				reslutDesc = "中度阳性";
				logger.error("CH4Con0 <= 12 && H2Con0 > 20 H2Con1 > 20 && H2Con1 <= 40 reslut=" + reslut
						+ " reslutDesc=" + reslutDesc);
			} else if (CH4Con1 > 12 && H2Con1 <= 20) {
				if (H2max > 80 || CH4max > 52) {
					reslut = 3;
					reslutDesc = "重度阳性";
				} else if ((H2max > 40 && H2max <= 80) || (CH4max > 32 && CH4max <= 52)) {
					reslut = 2;
					reslutDesc = "中度阳性";
				} else if ((H2max > 20 && H2max <= 40) || (CH4max > 12 && CH4max <= 32)) {
					reslut = 1;
					reslutDesc = "轻度阳性";
				} else if ((CH4max <= 12 && H2max <= 20)) {
					reslut = 5;
					reslutDesc = "阴性";
				}
				logger.error("CH4Con0 <= 12 && H2Con0 > 20 CH4Con1 > 12 && H2Con1 <= 20 reslut=" + reslut
						+ " reslutDesc=" + reslutDesc);
			} else if (CH4Con1 <= 12 && H2Con1 <= 20) {
				if (CH4Sub > 52 || H2Sub > 80 || CH4_H2Sub > 55) {
					reslut = 3;
					reslutDesc = "重度阳性";
				} else if ((CH4Sub > 32 && CH4Sub <= 52) || (H2Sub > 40 && H2Sub <= 80)
						|| (CH4_H2Sub > 35 && CH4_H2Sub <= 55)) {
					reslut = 2;
					reslutDesc = "中度阳性";
				} else if ((CH4Sub > 12 && CH4Sub <= 32) || (H2Sub > 20 && H2Sub <= 40)
						|| (CH4_H2Sub > 15 && CH4_H2Sub <= 35)) {
					reslut = 1;
					reslutDesc = "轻度阳性";
				} else if (CH4Sub <= 12 && H2Sub <= 20 && CH4_H2Sub <= 15) {
					reslut = 5;
					reslutDesc = "阴性";
				}
				logger.error("CH4Con0 <= 12 && H2Con0 > 20 CH4Con1 <= 12 && H2Con1 <= 20 reslut=" + reslut
						+ " reslutDesc=" + reslutDesc);
			}
		} else if (CH4Con0 > 12 && H2Con0 <= 20) {
			if (CH4Con1 > 32) {
				reslut = 3;
				reslutDesc = "重度阳性";
				logger.error(
						"CH4Con0 > 12 && H2Con0 <= 20 CH4Con1 > 32 reslut=" + reslut + " reslutDesc=" + reslutDesc);
			} else if (CH4Con1 > 12 && CH4Con1 <= 32) {
				reslut = 2;
				reslutDesc = "中度阳性";
				logger.error("CH4Con0 > 12 && H2Con0 <= 20 CH4Con1 > 12 && CH4Con1 <= 32 reslut=" + reslut
						+ " reslutDesc=" + reslutDesc);
			}
			if (CH4Con1 <= 12 && H2Con1 > 20) {
				if (CH4max > 52 || H2max > 80) {
					reslut = 3;
					reslutDesc = "重度阳性";
				} else if ((CH4max > 32 && CH4max <= 52) || (H2max > 40 && H2max <= 80)) {
					reslut = 2;
					reslutDesc = "中度阳性";
				} else if ((CH4max > 12 && CH4max <= 32) || (H2max > 20 && H2max <= 40)) {
					reslut = 1;
					reslutDesc = "轻度阳性";
				} else if (CH4max <= 12 && H2max <= 20) {
					reslut = 5;
					reslutDesc = "阴性";
				}
				logger.error("CH4Con0 > 12 && H2Con0 <= 20 CH4Con1 <= 12 && H2Con1 > 20 reslut=" + reslut
						+ " reslutDesc=" + reslutDesc);
			} else if (CH4Con1 <= 12 && H2Con1 <= 20) {
				if (CH4Sub > 52 || H2Sub > 80 || CH4_H2Sub > 55) {
					reslut = 3;
					reslutDesc = "重度阳性";
				} else if ((CH4Sub > 32 && CH4Sub <= 52) || (H2Sub > 40 && H2Sub <= 80)
						|| (CH4_H2Sub > 35 && CH4_H2Sub <= 55)) {
					reslut = 2;
					reslutDesc = "中度阳性";
				} else if ((CH4Sub > 12 && CH4Sub <= 32) || (H2Sub > 20 && H2Sub <= 40)
						|| (CH4_H2Sub > 15 && CH4_H2Sub <= 35)) {
					reslut = 1;
					reslutDesc = "轻度阳性";
				} else if (CH4Sub <= 12 && H2Sub <= 20 && CH4_H2Sub <= 15) {
					reslut = 5;
					reslutDesc = "阴性";
				}
				logger.error("CH4Con0 > 12 && H2Con0 <= 20 CH4Con1 <= 12 && H2Con1 <= 20 reslut=" + reslut
						+ " reslutDesc=" + reslutDesc);
			}
		} else if (CH4Con0 > 12 && H2Con0 > 20) {
			if (H2Con1 > 40 || CH4Con1 > 32) {
				reslut = 3;
				reslutDesc = "重度阳性";
				logger.error("CH4Con0 > 12 && H2Con0 > 20 H2Con1 > 40 || CH4Con1 > 32 reslut=" + reslut + " reslutDesc="
						+ reslutDesc);
			} else if ((H2Con1 > 20 && H2Con1 <= 40) || (CH4Con1 > 12 && CH4Con1 <= 32)) {
				reslut = 2;
				reslutDesc = "中度阳性";
				logger.error(
						"CH4Con0 > 12 && H2Con0 > 20 (H2Con1 > 12 && H2Con1 <= 40) || (CH4Con1 > 12 && CH4Con1 <= 32) reslut="
								+ reslut + " reslutDesc=" + reslutDesc);
			} else if (CH4Con1 <= 12 && H2Con1 <= 20) {
				if (CH4Sub > 52 || H2Sub > 80 || CH4_H2Sub > 55) {
					reslut = 3;
					reslutDesc = "重度阳性";
				} else if ((CH4Sub > 32 && CH4Sub <= 52) || (H2Sub > 40 && H2Sub <= 80)
						|| (CH4_H2Sub > 35 && CH4_H2Sub <= 55)) {
					reslut = 2;
					reslutDesc = "中度阳性";
				} else if ((CH4Sub > 12 && CH4Sub <= 32) || (H2Sub > 20 && H2Sub <= 40)
						|| (CH4_H2Sub > 15 && CH4_H2Sub <= 35)) {
					reslut = 1;
					reslutDesc = "轻度阳性";
				} else if (CH4Sub <= 12 && H2Sub <= 20 && CH4_H2Sub <= 15) {
					reslut = 5;
					reslutDesc = "阴性";
				}
				logger.error("CH4Con0 > 12 && H2Con0 > 20 CH4Con1 <= 12 && H2Con1 <= 20 reslut=" + reslut
						+ " reslutDesc=" + reslutDesc);
			}
		} else if (CH4Con0 <= 12 && H2Con0 <= 20) {
			int CH4Sub0 = CH4max - CH4Con0;
			int H2Sub0 = H2max - H2Con0;
			int CH4_H2Sub0 = (CH4max + H2max) - (CH4Con0 + H2Con0);
			logger.error("CH4Sub0=" + CH4Sub0 + ";H2Sub0=" + H2Sub0 + ";" + "CH4_H2Sub0=" + CH4_H2Sub0 + ";");
			if (CH4Sub0 > 52 || H2Sub0 > 80 || CH4_H2Sub0 > 55) {
				reslut = 3;
				reslutDesc = "重度阳性";
				logger.error("CH4Con0 <= 12 && H2Con0 <= 20 CH4Sub0 > 52 || H2Sub0 > 80 || CH4_H2Sub0 > 55 reslut="
						+ reslut + " reslutDesc=" + reslutDesc);
			} else if ((CH4Sub0 > 32 && CH4Sub0 <= 52) || (H2Sub0 > 40 && H2Sub0 <= 80)
					|| (CH4_H2Sub0 > 35 && CH4_H2Sub0 <= 55)) {
				reslut = 2;
				reslutDesc = "中度阳性";
				logger.error(
						"CH4Con0 <= 12 && H2Con0 <= 20 (CH4Sub0 > 32 && CH4Sub0 <= 52) || (H2Sub0 > 40 && H2Sub0 <= 80) || (CH4_H2Sub0 > 35 && CH4_H2Sub0 <= 55) reslut="
								+ reslut + " reslutDesc=" + reslutDesc);
			} else if ((CH4Sub0 > 12 && CH4Sub0 <= 32) || (H2Sub0 > 20 && H2Sub0 <= 40)
					|| (CH4_H2Sub0 > 15 && CH4_H2Sub0 <= 35)) {
				reslut = 1;
				reslutDesc = "轻度阳性";
				logger.error(
						"CH4Con0 <= 12 && H2Con0 <= 20 (CH4Sub0 > 12 && CH4Sub0 <= 32) || (H2Sub0 > 20 && H2Sub0 <= 40) || (CH4_H2Sub0 > 15 && CH4_H2Sub0 <= 35) reslut="
								+ reslut + " reslutDesc=" + reslutDesc);
			} else if (CH4Sub0 <= 12 && H2Sub0 <= 20 && CH4_H2Sub0 <= 15) {
				reslut = 5;
				reslutDesc = "阴性";
				logger.error("CH4Con0 <= 12 && H2Con0 <= 20 CH4Sub0 <= 12 && H2Sub0 <= 20 && CH4_H2Sub0 <= 15 reslut="
						+ reslut + " reslutDesc=" + reslutDesc);
			}
		} else {
			reslut = 5;
			reslutDesc = "阴性";
			logger.error("else reslut=" + reslut + " reslutDesc=" + reslutDesc);
		}

		if (isHospital == 1) {// 如果是医院,诊断结果不区分轻重度只有阳性阴性
			if (reslut == 1 || reslut == 2 || reslut == 3) {
				reslut = 4;
				reslutDesc = "阳性";
				logger.error("isHospital == 1 reslut == 1 || reslut == 2 || reslut == 3 reslut=" + reslut
						+ " reslutDesc=" + reslutDesc);
			}
		}

		JSONObject json = new JSONObject();
		json.put("reslut", reslut);
		json.put("reslutDesc", reslutDesc);
		return json;
	}

	public int saveCheckDevice(Integer id, String name, String code, Long hospitalId, Account account,
			Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(name)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入设备名称");
			return 0;
		}
		if (StringUtils.isBlank(code)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入设备编号");
			return 0;
		}
		if (hospitalId == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择所属机构");
			return 0;
		}
		CheckDevice record = new CheckDevice();
		record.setName(name);
		record.setCode(code);
		record.setHospitalId(hospitalId);
		if (id == null) {// 新增
			record.setCreatorId(account.getId());
			record.setCreateTime(new Date());
			checkDeviceMapper.insertSelective(record);
		} else {// 修改
			record.setId(id);
			record.setUpdateTime(new Date());
			checkDeviceMapper.updateByPrimaryKeySelective(record);
		}
		return 1;
	}

	public int delCheckDevice(Integer deviceId, Account account, Object apiResult) {
		if (deviceId == null) {
			ApiResult result = (ApiResult) apiResult;
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的设备");
			return 0;
		}
		CheckDevice record = new CheckDevice();
		record.setId(deviceId);
		record.setIsDel((byte) 1);
		record.setUpdateTime(new Date());
		checkDeviceMapper.updateByPrimaryKey(record);
		return 1;
	}

	public JSONObject getCheckDeviceList(Long hospitalId, Integer pageNo, Integer pageSize, Account account,
			Object apiResult) {
		JSONObject json = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isDel", 0);
		Integer totalCount = checkDeviceMapper.selectCountByMap(map);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("checkDeviceList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<CheckDevice> list = checkDeviceMapper.selectListByMap(map);
		for (CheckDevice checkDevice : list) {
			Hospital Hospital = hospitalMapper.selectByPrimaryKey(checkDevice.getHospitalId());
			checkDevice.setHospitalName(Hospital == null ? null : Hospital.getHospitalName());
		}
		json.put("page", page);
		json.put("checkDeviceList", list);
		return json;
	}

	public JSONObject checkAuditList(String sampleInfoQuery, Account loginAccount, Object apiResult) {
		logger.error(JSON.toJSONString(loginAccount));

		// ReportNeedAduit 报告是否需要审核 0:否 1:是
		// 先判断当前机构的检测信息是否需要审核,若不需要,则直接返回空
		JSONObject json = new JSONObject();
		Hospital hospital = hospitalMapper.selectByPrimaryKey(loginAccount.getHospitalId());
		if (hospital != null && hospital.getReportNeedAduit() == 0) {
			json.put("page", null);
			json.put("auditInfoList", null);
			return json;
		}
		SampleInfoQuery query = null;
		if (StringUtils.isNotBlank(sampleInfoQuery)) {
			query = JSONObject.parseObject(sampleInfoQuery, SampleInfoQuery.class);
		} else {
			query = new SampleInfoQuery();
		}

		List<Long> hospitalIds = customerManageService.getUserHostList(loginAccount);

		// 这里做权限判定
		if (hospitalIds == null) {
			// 主账号
		} else if (hospitalIds.size() == 0) {
			// 无权限
			hospitalIds.add(0L);
			query.setHospitalIdList(hospitalIds);
		} else {
			// 非主账号，有权限
			query.setHospitalIdList(hospitalIds);
		}
		logger.error(JSON.toJSONString(hospitalIds));

		// 审核列表只展示检测过的数据
		query.setCheckStatus((byte) 1);
		String keyword = query.getKeyword();
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", "");
			query.setKeyword("%" + keyword + "%");
		} else {
			query.setKeyword(null);
		}
		if (StringUtils.isBlank(query.getBeginDate())) {
			query.setBeginDate(null);
		}
		if (StringUtils.isBlank(query.getEndDate())) {
			query.setEndDate(null);
		}
		logger.error(JSON.toJSONString(query));

		Integer totalCount = reportInfoMapper.selectCountBySampleInfoQuery(query);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("auditInfoList", null);
			return json;
		}
		Page page = new Page(query.getPageNo(), query.getPageSize(), totalCount);
		query.setStartIndex(page.getStartIndex());
		List<ReportInfo> list = reportInfoMapper.selectListBySampleInfoQuery(query);

		List<JSONObject> auditInfoList = new ArrayList<JSONObject>();
		for (ReportInfo reportInfo : list) {
			JSONObject auditInfo = new JSONObject();
			auditInfo.put("id", reportInfo.getId());
			auditInfo.put("customerPhone", reportInfo.getCustomerPhone());// 用户手机号
			auditInfo.put("sampleCode", reportInfo.getSampleCode());// 采样编号
			auditInfo.put("sampleName", reportInfo.getSampleName());// 采样者姓名
			auditInfo.put("samplePhone", reportInfo.getSamplePhone());// 采样者手机号
			if (reportInfo.getSampleSex() != null) {// 采样者性别
				if (reportInfo.getSampleSex() == 1) {
					auditInfo.put("sampleSex", "男");
				} else if (reportInfo.getSampleSex() == 2) {
					auditInfo.put("sampleSex", "女");
				}
			}
			auditInfo.put("sampleAge", reportInfo.getSampleAge());// 采样者年龄
			auditInfo.put("submitTime", reportInfo.getSubmitTime() == null ? null
					: DateUtil.formatDate(reportInfo.getSubmitTime(), "yyyy-MM-dd"));// 采样提交时间
			auditInfo.put("checkTime", reportInfo.getCheckTime() == null ? null
					: DateUtil.formatDate(reportInfo.getCheckTime(), "yyyy-MM-dd"));// 检测时间
			Account checker = accountMapper.selectByPrimaryKey(reportInfo.getCheckAccountId());
			auditInfo.put("checkerName", checker == null ? null : checker.getName());// 检测员
			Account auditer = accountMapper.selectByPrimaryKey(reportInfo.getAuditAccountId());
			auditInfo.put("auditerName", auditer == null ? null : auditer.getName());// 审核员
			auditInfo.put("auditTime", reportInfo.getAuditTime() == null ? null
					: DateUtil.formatDate(reportInfo.getAuditTime(), "yyyy-MM-dd"));// 审核时间
			auditInfo.put("auditStatus", reportInfo.getAuditStatus());// 审核状态 0：待审核 1：已通过 2：待修改
			auditInfo.put("reportStatus", reportInfo.getReportStatus());// 报告状态 1：用户绑定 2：门店扫码 3：邮寄单号 4：乐为扫码 5：结果生成
			if (reportInfo.getAuditStatus() == 0) {
				auditInfo.put("auditStatusDesc", "待审核");
			} else if (reportInfo.getAuditStatus() == 1) {
				auditInfo.put("auditStatusDesc", "已通过");
			} else if (reportInfo.getAuditStatus() == 2) {
				auditInfo.put("auditStatusDesc", "待修改");
			}
			Hospital hospital2 = hospitalMapper.selectByPrimaryKey(reportInfo.getHospitalId());
			auditInfo.put("hospitalName", hospital2 == null ? null : hospital2.getHospitalName());// 送检机构名称
			auditInfoList.add(auditInfo);
		}
		json.put("page", page);
		json.put("auditInfoList", auditInfoList);
		return json;
	}

	@Transactional
	public int submitAuditInfo(String auditInfo, Integer operateType, Account loginAccount, Object apiResult) {
		if (StringUtils.isNotBlank(auditInfo)) {
			AuditInfoBo auditInfoBo = JSONObject.parseObject(auditInfo, AuditInfoBo.class);
			ApiResult result = (ApiResult) apiResult;
			if (auditInfoBo.getId() == null) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("参数id不可为空");
				return 0;
			}
			if (auditInfoBo.getHospitalRoomId() == null) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择送检科室");
				return 0;
			}
			if (auditInfoBo.getHospitalDoctorId() == null) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择送检医生");
				return 0;
			}
			if (auditInfoBo.getCheckDeviceId() == null) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择检测设备");
				return 0;
			}
			if (auditInfoBo.getCheckAccountId() == null) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择检测员");
				return 0;
			}
			if (StringUtils.isBlank(auditInfoBo.getSamplePhone())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者手机号");
				return 0;
			}
			if (StringUtils.isBlank(auditInfoBo.getSampleCode())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者编号");
				return 0;
			}
			if (StringUtils.isBlank(auditInfoBo.getSampleName())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者姓名");
				return 0;
			}
			Byte sampleSex = auditInfoBo.getSampleSex();
			if (StringUtils.isBlank(sampleSex)) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者性别");
				return 0;
			}
			if (StringUtils.isBlank(auditInfoBo.getSampleAge())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者年龄");
				return 0;
			}
			if (StringUtils.isBlank(auditInfoBo.getCheckSubstrateId())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择底物");
				return 0;
			}
			if (StringUtils.isBlank(auditInfoBo.getCheckSubstrateDosage())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择底物剂量");
				return 0;
			}
			if (StringUtils.isBlank(auditInfoBo.getCheckItemId())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择检测项目");
				return 0;
			}
			if (auditInfoBo.getHospitalId() == null) {
				// 若未选择送检门店,则后台默认当前账号所属的门店id
				auditInfoBo.setHospitalId(loginAccount.getHospitalId());
			}

			// 更新检测报告信息主表
			ReportInfo reportInfo = new ReportInfo();
			BeanUtils.copyProperties(auditInfoBo, reportInfo);
			// 0：待审核 1：已通过 2：待修改
			if (operateType == 1) {
				reportInfo.setAuditStatus((byte) 1);
			} else if (operateType == 2) {
				reportInfo.setAuditStatus((byte) 2);
			}
			reportInfo.setAuditAccountId(loginAccount.getId());
			reportInfo.setAuditTime(new Date());
			// 报告状态 1:用户绑定 2:门店扫码 3:邮寄单号 4:乐为扫码 5:结果生成
			reportInfo.setReportStatus(ReportStatus.RESULT_CREATE.getValue());// 报告状态改成结果生成
			reportInfo.setReportCreateTime(new Date());
			reportInfoMapper.updateByPrimaryKeySelective(reportInfo);

			// 更新疾病列表
			List<ReportIllness> illnessList = auditInfoBo.getIllnessList();
			reportIllnessMapper.deleteByReportInfoId(reportInfo.getId());
			for (ReportIllness illness : illnessList) {
				illness.setId(null);
				illness.setReportInfoId(reportInfo.getId());
				reportIllnessMapper.insertSelective(illness);
			}
			// 更新症状列表
			List<ReportSymptom> symptomList = auditInfoBo.getSymptomList();
			reportSymptomMapper.deleteByReportInfoId(reportInfo.getId());
			for (ReportSymptom symptom : symptomList) {
				symptom.setId(null);
				symptom.setReportInfoId(reportInfo.getId());
				reportSymptomMapper.insertSelective(symptom);
			}
			// 气袋检测数据
			reportCheckDataMapper.deleteByReportInfoId(reportInfo.getId());
			List<ReportCheckData> checkDataList = auditInfoBo.getCheckDataList();
			int checkPoint = 0;
			for (ReportCheckData reportCheckData : checkDataList) {
				reportCheckData.setId(null);
				reportCheckData.setCheckPoint(checkPoint);
				reportCheckData.setReportInfoId(reportInfo.getId());
				reportCheckData.setCreateTime(new Date());
				reportCheckDataMapper.insertSelective(reportCheckData);
				checkPoint++;
			}
			// 发送短信通知用户报告生成
			SMSUtil.sendReportNoticeSMS(reportInfo.getSamplePhone(), reportInfo.getSampleName());
			String content = "提交了审核信息,姓名:" + reportInfo.getSampleName() + ",检测编号:" + reportInfo.getSampleCode();
			sysLogService.addSysLog(loginAccount, content, new Date());

			// 判断如果是美年端的报告,则需要调用美年的保存信息接口
			Hospital hospital = hospitalMapper.selectByPrimaryKey(auditInfoBo.getHospitalId());
			if (hospital != null && hospital.getHospitalName().contains("美年")) {
				JSONObject json = new JSONObject();
				json.put("hospitalName", hospital.getHospitalName());// 门店名称
				json.put("hospitalCode", hospital.getHospitalCode());// 门店编码
				json.put("name", auditInfoBo.getSampleName());// 姓名
				Byte sex = auditInfoBo.getSampleSex();
				if (sex == 1) {
					json.put("sex", "M");// 男
				} else if (sex == 2) {
					json.put("sex", "F");// 女
				}
				json.put("vid", auditInfoBo.getSampleCode());// 采样编码
				json.put("itemId", "10160");// 项目编码(固定)
				json.put("itemName", "肠道菌群无创检测");// 项目名称(固定)
				// json.put("npMark", "阳性");//阴阳性标识
				json.put("reportNum", "4");// 报告张数
				MeiNianReportUtil.addCheckInfo(json);

				// 报告名称
				// 美年端为'全肠道菌群无创吹气检查报告',其他为'甲烷氢呼气检查报告解析'
				String reportName = "全肠道菌群无创吹气检查报告";
				if (!hospital.getHospitalName().contains("美年")) {
					reportName = "甲烷氢呼气检查报告解析";
				}
				ReportInfo update = new ReportInfo();
				update.setId(reportInfo.getId());
				update.setReportName(reportName);
				reportInfoMapper.updateByPrimaryKeySelective(update);
			}
		}
		return 1;
	}

	public JSONObject auditInfoDetail(Long id, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (id == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数id为空");
			return null;
		}
		JSONObject json = new JSONObject();
		ReportInfo reportInfo = reportInfoMapper.selectByPrimaryKey(id);
		if (reportInfo != null) {
			json.put("id", reportInfo.getId());
			json.put("reportName", reportInfo.getReportName());// 报告名称
			json.put("sysReportCode", reportInfo.getSysReportCode());// 系统报告编号
			json.put("auditAccountId", reportInfo.getAuditAccountId());// 审核员id
			Account auditer = accountMapper.selectByPrimaryKey(reportInfo.getAuditAccountId());
			json.put("auditerName", auditer == null ? null : auditer.getName());// 审核员
			json.put("auditStatus", reportInfo.getAuditStatus());// 审核状态
			if (reportInfo.getAuditStatus() == 0) {
				json.put("auditStatusDesc", "待审核");
			} else if (reportInfo.getAuditStatus() == 1) {
				json.put("auditStatusDesc", "已通过");
			} else if (reportInfo.getAuditStatus() == 2) {
				json.put("auditStatusDesc", "待修改");
			}
			json.put("auditTime", reportInfo.getAuditTime());// 审核时间
			// 机构信息
			json.put("hospitalId", reportInfo.getHospitalId());// 机构id
			Hospital hospital = hospitalMapper.selectByPrimaryKey(reportInfo.getHospitalId());

			json.put("hospitalName", hospital == null ? null : hospital.getHospitalName());// 机构名称
			json.put("hospitalId", hospital == null ? null : hospital.getId());// 机构名称
			json.put("logoUrl", hospital == null ? null : hospital.getLogoUrl());// 机构名称

			json.put("hospitalRoomId", reportInfo.getHospitalRoomId());// 送检科室id
			json.put("hospitalDoctorId", reportInfo.getHospitalDoctorId());// 送检医生id
			json.put("checkDeviceId", reportInfo.getCheckDeviceId());// 检测设备id
			json.put("checkAccountId", reportInfo.getCheckAccountId());// 检测设备id
			// 基础信息
			json.put("customerPhone", reportInfo.getCustomerPhone());// 用户手机号
			json.put("samplePhone", reportInfo.getSamplePhone());// 采样者手机号
			json.put("sampleCode", reportInfo.getSampleCode());// 采样者编号
			json.put("sampleName", reportInfo.getSampleName());// 采样者姓名
			json.put("sampleSex", reportInfo.getSampleSex());// 采样者性别
			json.put("sampleAge", reportInfo.getSampleAge());// 采样者年龄
			json.put("sampleHeight", reportInfo.getSampleHeight());// 采样者身高
			json.put("sampleWeight", reportInfo.getSampleWeight());// 采样者体重
			json.put("submitTime", reportInfo.getSubmitTime() == null ? null
					: DateUtil.formatDate(reportInfo.getSubmitTime(), "yyyy-MM-dd"));// 采样时间
			json.put("checkTime", reportInfo.getCheckTime() == null ? null
					: DateUtil.formatDate(reportInfo.getCheckTime(), "yyyy-MM-dd"));// 检测时间
			// 检测项目及底物
			json.put("checkItemId", reportInfo.getCheckItemId());// 检测项目id
			CheckItem checkItem = checkItemMapper.selectByPrimaryKey(reportInfo.getCheckItemId());
			json.put("checkItemName", checkItem == null ? null : checkItem.getName());// 项目名称
			Substrate substrate = substrateMapper.selectByPrimaryKey(reportInfo.getCheckSubstrateId());
			json.put("checkSubstrateId", reportInfo.getCheckSubstrateId());// 检测项目底物id
			json.put("checkSubstrateName", substrate == null ? null : substrate.getName());// 底物名称
			json.put("checkSubstrateDosage", reportInfo.getCheckSubstrateDosage());// 检测项目底物剂量
			// 底物和剂量(一个检测项目会有多组底物和剂量)
			List<CheckItemSubstrate> substrateList = checkItemSubstrateMapper
					.selectListByItemId(reportInfo.getCheckItemId());
			json.put("substrateList", substrateList);
			json.put("reportStatus", reportInfo.getReportStatus());
			// 采样疾病信息
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("reportInfoId", id);
			List<ReportIllness> illnessList = reportIllnessMapper.selectListByMap(map);
			List<ReportSymptom> symptomList = reportSymptomMapper.selectListByMap(map);
			json.put("illnessList", illnessList);// 疾病列表
			json.put("symptomList", symptomList);// 症状列表
			json.put("takeAntibiotics", reportInfo.getTakeAntibiotics());// 近一个月是否服用抗生素 0:否 1:是
			json.put("antibioticsName", reportInfo.getAntibioticsName());// 所服用抗生素的药品名称
			json.put("helicobacterPyloriCheck", reportInfo.getHelicobacterPyloriCheck());// 幽门螺旋杆菌检测 0:未检测 1:阳性 2:阴性
			json.put("hpCheckResult", reportInfo.getHpCheckResult());// 幽门螺旋杆菌检测结果
			json.put("gastroscopeEnteroscopyCheck", reportInfo.getGastroscopeEnteroscopyCheck());// 胃镜/肠镜检测 0:未检测 1:胃镜
																									// 2:肠镜
			json.put("geCheckResult", reportInfo.getGeCheckResult());// 胃镜/肠镜检测结果
			json.put("foodMedicineAllergy", reportInfo.getFoodMedicineAllergy());// 食物/药物过敏 0:否 1:是
			json.put("allergyFood", reportInfo.getAllergyFood());// 过敏物品名称
			// 检测数据
			List<ReportCheckData> checkDataList = reportCheckDataMapper.selectListByMap(map);

			if (checkDataList == null || checkDataList.size() == 0) {
				checkDataList = new ArrayList<ReportCheckData>();

				// 若未查询到检测数据,则取出默认的气袋数据规格
				List<GasBagDefault> gasBagDefault = gasBagDefaultMapper.selectAllList();
				for (GasBagDefault gasBag : gasBagDefault) {

					if (reportInfo.getSampleAge() != null && reportInfo.getSampleAge().intValue() <= 18
							&& gasBag.getType().intValue() == 1) {
						ReportCheckData checkData = new ReportCheckData();
						checkData.setCheckTime(gasBag.getCheckTime());
						checkData.setTimeSpace(gasBag.getTimeSpace());
						checkDataList.add(checkData);
					}

					if (reportInfo.getSampleAge() != null && reportInfo.getSampleAge().intValue() > 18
							&& gasBag.getType().intValue() == 0) {
						ReportCheckData checkData = new ReportCheckData();
						checkData.setCheckTime(gasBag.getCheckTime());
						checkData.setTimeSpace(gasBag.getTimeSpace());
						checkDataList.add(checkData);
					}
				}
			}
			json.put("gasCheckResult", reportInfo.getGasCheckResult());// 气体检测结果描述
			json.put("reportResult", reportInfo.getReportResult());// 报告结果标识
			json.put("reportResultDescription", reportInfo.getReportResultDescription());// 报告结果描述
			json.put("reportDataAnalysis", reportInfo.getReportDataAnalysis());// 数据分析
			json.put("interventionSuggestion", reportInfo.getInterventionSuggestion());// 数据分析
			List<JSONObject> jsonList = new ArrayList<JSONObject>();
			for (ReportCheckData reportCheckData : checkDataList) {
				JSONObject checkData = new JSONObject();
				checkData.put("timeSpace", reportCheckData.getTimeSpace());// 时间间隔
				checkData.put("checkTime", reportCheckData.getCheckTime());// 检测时间
				checkData.put("h2Concentration", reportCheckData.getH2Concentration());// 氢气浓度
				checkData.put("ch4Concentration", reportCheckData.getCh4Concentration());// 甲烷浓度
				checkData.put("co2Concentration", reportCheckData.getCo2Concentration());// 二氧化碳浓度
				jsonList.add(checkData);
			}
			json.put("checkDataList", jsonList);// 检测数据
			// 困扰健康的问题
			json.put("besetHealthProblem", reportInfo.getBesetHealthProblem());
			json.put("adviceMsg", reportInfo.getAdviceMsg());
		}
		return json;
	}

	@Transactional
	public int updateAuditStatus(Long id, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (id == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数id为空");
			return 0;
		}
		ReportInfo record = new ReportInfo();
		record.setId(id);
		record.setAuditStatus((byte) 2);// 0：待审核 1：已通过 2：待修改
		reportInfoMapper.updateByPrimaryKeySelective(record);
		return 1;
	}

	public JSONObject getItemSubstrateList(Integer itemId, Account account, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (itemId == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数itemId为空");
			return null;
		}
		JSONObject json = new JSONObject();
		List<CheckItemSubstrate> substrateList = checkItemSubstrateMapper.selectListByItemId(itemId);
		json.put("substrateList", substrateList);// 底物剂量列表
		return json;
	}

	public JSONObject checkSubstrateIsUsed(Integer itemId, Account account, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (itemId == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数为空");
			return null;
		}
		List<JSONObject> resultList = new ArrayList<JSONObject>();
		JSONObject resultJson = new JSONObject();
		List<CheckItemSubstrate> substrateList = checkItemSubstrateMapper.selectListByItemId(itemId);
		for (CheckItemSubstrate checkItemSubstrate : substrateList) {
			JSONObject json = new JSONObject();
			json.put("substrateId", checkItemSubstrate.getSubstrateId());
			json.put("substrateName", checkItemSubstrate.getSubstrateName());
			json.put("substrateDosage", checkItemSubstrate.getSubstrateDosage());
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("checkSubstrateIsUsed", 1);// 该参数为此接口查询专用
			paramMap.put("checkSubstrateId", checkItemSubstrate.getSubstrateId());
			Integer count = reportInfoMapper.selectCountByMap(paramMap);
			if (count != null && count > 0) {
				json.put("isUsed", 1);
			} else {
				json.put("isUsed", 0);
			}
			resultList.add(json);
		}
		resultJson.put("resultList", resultList);
		return resultJson;
	}

	@Autowired
	private ShowFieldMapper showFieldMapper;

	public JSONObject getShowFieldList(Long reportId, Account loginAccount, Object result) {

		String[] mdField = null;
		ReportInfo reportInfo = null;

		if (reportId != null) {
			reportInfo = reportInfoMapper.selectByPrimaryKey(reportId);
			if (reportInfo != null) {
				if (reportInfo.getHospitalId() != null) {
					Hospital hospital = hospitalMapper.selectByPrimaryKey(reportInfo.getHospitalId());
					if (hospital.getShowFieldIds() != null && StringUtils.isNotBlank(hospital.getShowFieldIds())) {
						mdField = hospital.getShowFieldIds().split("\\,");
					}
				}
			}
		}

		List<JSONObject> list = new ArrayList<JSONObject>();
		if (loginAccount != null) {// 说明是B端账号调用
			Account account = accountMapper.selectByPrimaryKey(loginAccount.getId());
			if (account != null) {
				String showFieldIds = account.getShowFieldIds();
				if (StringUtils.isNotBlank(showFieldIds)) {
					String[] arr = showFieldIds.split("\\,");
					for (String id : arr) {

						boolean has = true;
						if (mdField != null) {
							has = false;
							for (String f : mdField) {
								if (id.equals(f)) {
									has = true;
									break;
								}
							}
						}
						if (!has) {
							continue;
						}

						ShowField showField = showFieldMapper.selectByPrimaryKey(Integer.valueOf(id));
						if (showField != null) {
							JSONObject json = new JSONObject();
							json.put("id", showField.getId());
							json.put("name", showField.getName());
							list.add(json);
						}
					}
				}
			}
		} else {
			if (reportId != null) {// 说明是C端报告详情中用
				if (reportInfo != null) {
					// 先看有没有审核员,有则查询审核员账号下能看到的字段
					if (reportInfo.getAuditAccountId() != null) {
						Account auditer = accountMapper.selectByPrimaryKey(reportInfo.getAuditAccountId());
						if (auditer != null) {
							String showFieldIds = auditer.getShowFieldIds();
							if (StringUtils.isNotBlank(showFieldIds)) {
								String[] arr = showFieldIds.split("\\,");
								for (String id : arr) {

									boolean has = true;
									if (mdField != null) {
										has = false;
										for (String f : mdField) {
											if (id.equals(f)) {
												has = true;
												break;
											}
										}
									}
									if (!has) {
										continue;
									}

									ShowField showField = showFieldMapper.selectByPrimaryKey(Integer.valueOf(id));
									if (showField != null) {
										JSONObject json = new JSONObject();
										json.put("id", showField.getId());
										json.put("name", showField.getName());
										list.add(json);
									}
								}
							}
						}
						// 若没有审核员,则查询检测员账号下能看到的字段
					} else {
						Account checker = accountMapper.selectByPrimaryKey(reportInfo.getCheckAccountId());
						if (checker != null) {
							String showFieldIds = checker.getShowFieldIds();
							if (StringUtils.isNotBlank(showFieldIds)) {
								String[] arr = showFieldIds.split("\\,");
								for (String id : arr) {
									boolean has = true;
									if (mdField != null) {
										has = false;
										for (String f : mdField) {
											if (id.equals(f)) {
												has = true;
												break;
											}
										}
									}
									if (!has) {
										continue;
									}

									ShowField showField = showFieldMapper.selectByPrimaryKey(Integer.valueOf(id));
									if (showField != null) {
										JSONObject json = new JSONObject();
										json.put("id", showField.getId());
										json.put("name", showField.getName());
										list.add(json);
									}
								}
							}
						}
					}
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("showFieldList", list);
		return json;
	}
}
