package com.lewe.web.controller.sysmanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.check.CheckDevice;
import com.lewe.bean.check.CheckItem;
import com.lewe.bean.check.Substrate;
import com.lewe.bean.hospital.Hospital;
import com.lewe.bean.hospital.HospitalDoctor;
import com.lewe.bean.hospital.HospitalGroup;
import com.lewe.bean.hospital.HospitalRoom;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.Channel;
import com.lewe.bean.sys.Illness;
import com.lewe.bean.sys.Role;
import com.lewe.bean.sys.Symptom;
import com.lewe.dao.check.CheckDeviceMapper;
import com.lewe.dao.check.CheckItemMapper;
import com.lewe.dao.check.SubstrateMapper;
import com.lewe.dao.hospital.HospitalDoctorMapper;
import com.lewe.dao.hospital.HospitalGroupMapper;
import com.lewe.dao.hospital.HospitalMapper;
import com.lewe.dao.hospital.HospitalRoomMapper;
import com.lewe.dao.sys.AccountMapper;
import com.lewe.dao.sys.ChannelMapper;
import com.lewe.dao.sys.IllnessMapper;
import com.lewe.dao.sys.RoleMapper;
import com.lewe.dao.sys.SymptomMapper;
import com.lewe.service.customer.ICustomerManageService;
import com.lewe.service.hospital.IHospitalService;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.StringUtils;
import com.lewe.util.common.constants.AccountType;
import com.lewe.web.controller.common.BaseController;

/**
 * 门店/机构相关的控制器
 * 
 * @author 小辉
 *
 */
@Controller
@RequestMapping("/hospital/")
public class HospitalController extends BaseController {

	@Autowired
	private IHospitalService hospitalService;

	@Autowired
	private ICustomerManageService customerManageService;

	public static final Logger logger = LoggerFactory.getLogger(HospitalController.class);

	/**
	 * 新增或修改 门店/机构
	 */
	@ResponseBody
	@RequestMapping("saveHospital")
	public ApiResult saveHospital(HttpServletRequest request, HttpServletResponse response) {
		String hospitalStr = request.getParameter("hospital");
		ApiResult result = new ApiResult();
		if (StringUtils.isBlank(hospitalStr)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请求参数为空");
			return result;
		}
		Account account = getSessionAccount(request, result);
		if (account != null) {
			hospitalService.saveHospital(hospitalStr, account, result);
		}
		return result;
	}

	/**
	 * 删除门店
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delHospital")
	public ApiResult delHospital(HttpServletRequest request, HttpServletResponse response) {
		String hospitalId = request.getParameter("hospitalId");
		ApiResult result = new ApiResult();
		if (StringUtils.isBlank(hospitalId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的门店");
			return result;
		}
		Account account = getSessionAccount(request, result);
		if (account != null) {
			hospitalService.delHospital(hospitalId, account, result);
		}
		return result;
	}

	/**
	 * 获取门店详情
	 */
	@ResponseBody
	@RequestMapping("getHospitalDetail")
	public ApiResult getHospitalDetail(HttpServletRequest request, HttpServletResponse response) {
		String hospitalId = request.getParameter("hospitalId");
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request, result);
		if (account != null) {
			result.setData(hospitalService.getHospitalDetail(hospitalId, account, result));
		}
		return result;
	}

	/**
	 * 分页查询门店机构列表
	 */
	@ResponseBody
	@RequestMapping("getHospitalList")
	public ApiResult getHospitalList(HttpServletRequest request,
			@RequestParam(required = false, value = "pageNo") Integer pageNo,
			@RequestParam(required = false, value = "pageSize") Integer pageSize,
			@RequestParam(required = false, value = "channelId") Integer channelId) {
		String keyword = request.getParameter("keyword");
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request, result);
		if (account != null) {
			JSONObject json = hospitalService.getHospitalList(keyword, channelId, pageNo, pageSize, account.getId(),
					result);
			result.setData(json);
		}
		return result;
	}

	/**
	 * 保存科室
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveHospitalRoom")
	public ApiResult saveHospitalRoom(HttpServletRequest request, HttpServletResponse response) {
		String hospitalRoomStr = request.getParameter("hospitalRoom");
		ApiResult result = new ApiResult();
		if (StringUtils.isBlank(hospitalRoomStr)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请求参数为空");
			return result;
		}
		Account account = getSessionAccount(request, result);
		if (account != null) {
			hospitalService.saveHospitalRoom(hospitalRoomStr, account, result);
		}
		return result;
	}

	/**
	 * 删除科室
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delHospitalRoom")
	public ApiResult delHospitalRoom(HttpServletRequest request, HttpServletResponse response) {
		String roomId = request.getParameter("roomId");
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request, result);
		if (account != null) {
			hospitalService.delHospitalRoom(roomId, account, result);
		}
		return result;
	}

	/**
	 * 分页查询科室列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param hospitalId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getHospitalRoomList")
	public ApiResult getHospitalRoomList(HttpServletRequest request,
			@RequestParam(required = false, value = "pageNo") Integer pageNo,
			@RequestParam(required = false, value = "pageSize") Integer pageSize,
			@RequestParam(required = false, value = "hospitalId") Integer hospitalId) {
		ApiResult result = new ApiResult();
		String keyword = request.getParameter("keyword");
		Account account = getSessionAccount(request, result);
		if (account != null) {
			JSONObject json = hospitalService.getHospitalRoomList(keyword, hospitalId, pageNo, pageSize, account);
			result.setData(json);
		}
		return result;
	}

	/**
	 * 保存医生
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveHospitalDoctor")
	public ApiResult saveHospitalDoctor(HttpServletRequest request, HttpServletResponse response) {
		String hospitalDoctorStr = request.getParameter("hospitalDoctor");
		ApiResult result = new ApiResult();
		if (StringUtils.isBlank(hospitalDoctorStr)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请求参数为空");
			return result;
		}
		Account account = getSessionAccount(request, result);
		if (account != null) {
			hospitalService.saveHospitalDoctor(hospitalDoctorStr, account, result);
		}
		return result;
	}

	/**
	 * 删除医生
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delHospitalDoctor")
	public ApiResult delHospitalDoctor(HttpServletRequest request, HttpServletResponse response) {
		String doctorId = request.getParameter("doctorId");
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request, result);
		if (account != null) {
			hospitalService.delHospitalDoctor(doctorId, account, result);
		}
		return result;
	}

	/**
	 * 分页查询医生列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getHospitalDoctorList")
	public ApiResult getHospitalDoctorList(HttpServletRequest request,
			@RequestParam(required = false, value = "pageNo") Integer pageNo,
			@RequestParam(required = false, value = "pageSize") Integer pageSize) {
		ApiResult result = new ApiResult();
		String keyword = request.getParameter("keyword");
		Account account = getSessionAccount(request, result);
		if (account != null) {
			JSONObject json = hospitalService.getHospitalDoctorList(keyword, pageNo, pageSize, account);
			result.setData(json);
		}
		return result;
	}

	/**
	 * 新增或修改 门店组
	 */
	@ResponseBody
	@RequestMapping("saveHospitalGroup")
	public ApiResult saveHospitalGroup(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("name");
		String groupId = request.getParameter("groupId");
		String hospitalIds = request.getParameter("hospitalIds");
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request, result);
		if (account != null) {
			hospitalService.saveHospitalGroup(groupId, name, hospitalIds, account, result);
		}
		return result;
	}

	/**
	 * 删除门店组
	 */
	@ResponseBody
	@RequestMapping("delHospitalGroup")
	public ApiResult delHospitalGroup(HttpServletRequest request, HttpServletResponse response) {
		String groupId = request.getParameter("groupId");
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request, result);
		if (account != null) {
			hospitalService.delHospitalGroup(groupId, account, result);
		}
		return result;
	}

	/**
	 * 分页查询门店组列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getHospitalGroupList")
	public ApiResult getHospitalGroupList(HttpServletRequest request,
			@RequestParam(required = false, value = "pageNo") Integer pageNo,
			@RequestParam(required = false, value = "pageSize") Integer pageSize) {
		ApiResult result = new ApiResult();
		Account account = getSessionAccount(request, result);
		if (account != null) {
			JSONObject json = hospitalService.getHospitalGroupList(pageNo, pageSize, account);
			result.setData(json);
		}
		return result;
	}

	@Autowired
	private ChannelMapper channelMapper;
	@Autowired
	private HospitalMapper hospitalMapper;
	@Autowired
	private HospitalRoomMapper hospitalRoomMapper;
	@Autowired
	private HospitalDoctorMapper hospitalDoctorMapper;
	@Autowired
	private HospitalGroupMapper hospitalGroupMapper;
	@Autowired
	private CheckItemMapper checkItemMapper;
	@Autowired
	private SubstrateMapper substrateMapper;
	@Autowired
	private IllnessMapper illnessMapper;
	@Autowired
	private SymptomMapper symptomMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private CheckDeviceMapper checkDeviceMapper;
	@Autowired
	private AccountMapper accountMapper;

	/**
	 * 获取数据下拉列表 注:列表数据只返回id和name两个字段
	 */
	@ResponseBody
	@RequestMapping("getSelectList")
	public ApiResult getSelectList(HttpServletRequest request, HttpServletResponse response) {
		String type = request.getParameter("type");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isDel", 0);// 仅查询没删除的数据
		ApiResult result = new ApiResult();
		List<JSONObject> list = new ArrayList<JSONObject>();
		if ("8".equals(type)) {
			// 症状列表(C端 问卷信息2页面使用,无需判断登录状态)
			List<Symptom> symptomList = symptomMapper.selectListByMap(map);
			for (Symptom symptom : symptomList) {
				JSONObject json = new JSONObject();
				json.put("id", symptom.getId());
				json.put("name", symptom.getName());
				list.add(json);
			}
		} else {
			Account loginAccount = getSessionAccount(request, result);
			if (loginAccount != null) {
				if ("1".equals(type)) {// 渠道下拉列表
					List<Channel> channelList = channelMapper.selectByMap(map);
					for (Channel channel : channelList) {
						JSONObject json = new JSONObject();
						json.put("id", channel.getId());
						json.put("name", channel.getName());
						list.add(json);
					}
				} else if ("2".equals(type)) {// 门店机构下拉列表
					Map<String, Object> mapHospital = new HashMap<String, Object>();
					mapHospital.put("isDel", 0);// 仅查询没删除的数据

					List<Long> hospitalIds = customerManageService.getUserHostList(loginAccount);
					if(hospitalIds != null && hospitalIds.size() == 0){
						hospitalIds.add(0L);
					}

					if(hospitalIds != null ){
						mapHospital.put("idList",hospitalIds);
					}
					logger.error(JSON.toJSONString(mapHospital));
					
					List<Hospital> hospitalList = hospitalMapper.selectListByMap(mapHospital);
					for (Hospital hospital : hospitalList) {
						JSONObject json = new JSONObject();
						json.put("id", hospital.getId());
						json.put("name", hospital.getHospitalName());
						list.add(json);
					}
				} else if ("3".equals(type)) {// 科室下拉列表
					
					String hospitalId = request.getParameter("hospitalId");
					if(!StringUtils.isBlank(hospitalId)){
						map.put("hospitalId", Long.parseLong(hospitalId));
					}

					List<Long> hospitalIds = customerManageService.getUserHostList(loginAccount);
					if(hospitalIds != null && hospitalIds.size() == 0){
						hospitalIds.add(0L);
					}
					map.put("hospitalIdList", hospitalIds);

					List<HospitalRoom> roomList = hospitalRoomMapper.selectListByMap(map);
					for (HospitalRoom hospitalRoom : roomList) {
						JSONObject json = new JSONObject();
						json.put("id", hospitalRoom.getId());
						json.put("name", hospitalRoom.getRoomName());
						list.add(json);
					}
				} else if ("4".equals(type)) {// 医生下拉列表
					String hospitalId = request.getParameter("hospitalId");
					if(!StringUtils.isBlank(hospitalId)){
						map.put("hospitalId", Long.parseLong(hospitalId));
					}

					List<Long> hospitalIds = customerManageService.getUserHostList(loginAccount);
					if(hospitalIds != null && hospitalIds.size() == 0){
						hospitalIds.add(0L);
					}
					map.put("hospitalIdList", hospitalIds);
					
					List<HospitalDoctor> doctorList = hospitalDoctorMapper.selectListByMap(map);
					for (HospitalDoctor hospitalDoctor : doctorList) {
						JSONObject json = new JSONObject();
						json.put("id", hospitalDoctor.getId());
						json.put("name", hospitalDoctor.getDoctorName());
						list.add(json);
					}
				} else if ("5".equals(type)) {// 检测项目下拉列表

					Map<String, Object> mapForCheck = new HashMap<String, Object>();
					mapForCheck.put("isDel", 0);// 仅查询没删除的数据

					List<Long> hospitalIds = customerManageService.getUserHostList(loginAccount);
					List<Long> checkIds = null;

					if (hospitalIds == null || !hospitalIds.isEmpty()) {

						if (hospitalIds != null && !hospitalIds.isEmpty()) {
							checkIds = new ArrayList<Long>();
							Map<String, Object> mapForHost = new HashMap<String, Object>();
							mapForHost.put("idList", hospitalIds);

							List<Hospital> hospitalList = hospitalMapper.selectListByMap(mapForHost);
							for (Hospital hospital : hospitalList) {
								if (StringUtils.isBlank(hospital.getCheckItemIds())) {
									continue;
								}
								String[] allAry = hospital.getCheckItemIds().split(",");
								for (String innerId : allAry) {
									if (StringUtils.isBlank(innerId)) {
										continue;
									}
									checkIds.add(Long.parseLong(innerId));
								}
							}

							if (checkIds.isEmpty()) {
								checkIds.add(Long.parseLong("0"));
							}
						}

						if (checkIds != null) {
							mapForCheck.put("idList", checkIds);
						}

						List<CheckItem> checkItemList = checkItemMapper.selectListByMap(mapForCheck);
						for (CheckItem checkItem : checkItemList) {
							JSONObject json = new JSONObject();
							json.put("id", checkItem.getId());
							json.put("name", checkItem.getName());
							list.add(json);
						}
					}

				} else if ("6".equals(type)) {// 底物下拉列表
					List<Substrate> substrateList = substrateMapper.selectList();
					for (Substrate substrate : substrateList) {
						JSONObject json = new JSONObject();
						json.put("id", substrate.getId());
						json.put("name", substrate.getName());
						list.add(json);
					}
				} else if ("7".equals(type)) {// 疾病下拉列表
					List<Illness> illnessList = illnessMapper.selectListByMap(map);
					for (Illness illness : illnessList) {
						JSONObject json = new JSONObject();
						json.put("id", illness.getId());
						json.put("name", illness.getName());
						list.add(json);
					}
				} else if ("9".equals(type)) {// 门店组下拉列表
					List<HospitalGroup> hospitalGroups = hospitalGroupMapper.selectListByMap(map);
					for (HospitalGroup group : hospitalGroups) {
						JSONObject json = new JSONObject();
						json.put("id", group.getId());
						json.put("name", group.getName());
						list.add(json);
					}
				} else if ("10".equals(type)) {// 角色下拉列表
					List<Role> RoleList = roleMapper.selectListByMap(map);
					for (Role role : RoleList) {
						JSONObject json = new JSONObject();
						json.put("id", role.getId());
						json.put("name", role.getName());
						list.add(json);
					}
				} else if ("11".equals(type)) {// 设备下拉列表
					List<CheckDevice> deviceList = checkDeviceMapper.selectListByMap(map);
					for (CheckDevice checkDevice : deviceList) {
						JSONObject json = new JSONObject();
						json.put("id", checkDevice.getId());
						json.put("name", checkDevice.getName());
						json.put("code", checkDevice.getCode());
						list.add(json);
					}
				} else if ("12".equals(type)) {// 检测员下拉列表
					if (loginAccount != null && loginAccount.getAccountType() != AccountType.SUPERADMIN.getValue()) {
						map.put("hospitalId", loginAccount.getHospitalId());
					}
					List<Account> accountList = accountMapper.selectListByMap(map);
					for (Account account : accountList) {
						JSONObject json = new JSONObject();
						json.put("id", account.getId());
						if (account.getStatus() == 2) {
							json.put("name", account.getName() + "(账号被冻结)");
						} else {
							json.put("name", account.getName());
						}
						list.add(json);
					}
				}
			}
		}
		result.setData(list);
		return result;
	}
}
