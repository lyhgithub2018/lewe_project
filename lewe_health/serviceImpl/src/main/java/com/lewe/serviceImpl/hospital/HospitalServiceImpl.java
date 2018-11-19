package com.lewe.serviceImpl.hospital;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.check.CheckItem;
import com.lewe.bean.hospital.Hospital;
import com.lewe.bean.hospital.HospitalDoctor;
import com.lewe.bean.hospital.HospitalGroup;
import com.lewe.bean.hospital.HospitalLinkman;
import com.lewe.bean.hospital.HospitalRoom;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.Channel;
import com.lewe.dao.check.CheckItemMapper;
import com.lewe.dao.hospital.HospitalDoctorMapper;
import com.lewe.dao.hospital.HospitalGroupMapper;
import com.lewe.dao.hospital.HospitalLinkmanMapper;
import com.lewe.dao.hospital.HospitalMapper;
import com.lewe.dao.hospital.HospitalRoomMapper;
import com.lewe.dao.sys.AccountMapper;
import com.lewe.dao.sys.ChannelMapper;
import com.lewe.service.hospital.IHospitalService;
import com.lewe.service.sys.ISysLogService;
import com.lewe.serviceImpl.hospital.bo.HospitalBo;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.Page;
import com.lewe.util.common.StringUtils;
import com.lewe.util.common.constants.AccountType;

@Service("hospitalService")
public class HospitalServiceImpl implements IHospitalService{

	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private ChannelMapper channelMapper;
	@Autowired
	private CheckItemMapper checkItemMapper;
	@Autowired
	private HospitalMapper hospitalMapper;
	@Autowired
	private HospitalRoomMapper hospitalRoomMapper;
	@Autowired
	private HospitalDoctorMapper hospitalDoctorMapper;
	@Autowired
	private HospitalGroupMapper hospitalGroupMapper;
	@Autowired
	private HospitalLinkmanMapper hospitalLinkmanMapper;
	@Autowired
	private ISysLogService sysLogService;
	
	@Transactional
	public int saveChannel(String name, String code, String channelId, Account loginAccount,Object apiResult) {
		ApiResult result = (ApiResult)apiResult;
		if(StringUtils.isBlank(name)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入渠道名称");
			return 0;
		}
		if(StringUtils.isBlank(code)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入渠道编号");
			return 0;
		}
		Channel channel = new Channel();
		if(StringUtils.isBlank(channelId)) {//新增
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", name);
			List<Channel> list = channelMapper.selectByMap(map);
			if(list!=null && list.size()>0) {
				result.setCode(BizCode.DATA_EXIST);
				result.setMessage("渠道名称【"+name+"】已存在,请重新输入");
				return 0;
			}
			map.remove("name");
			map.put("code", code);
			List<Channel> list2 = channelMapper.selectByMap(map);
			if(list2!=null && list2.size()>0) {
				result.setCode(BizCode.DATA_EXIST);
				result.setMessage("渠道编号【"+code+"】已存在,请重新输入");
				return 0;
			}
			channel.setName(name);
			channel.setCode(code);
			channel.setCreatorId(loginAccount.getId());
			channel.setCreateTime(new Date());
			channelMapper.insertSelective(channel);
			String content = "新增了一个渠道,渠道名称:"+name;
			sysLogService.addSysLog(loginAccount, content, new Date());
		}else {//修改
			channel.setId(Integer.valueOf(channelId));
			channel.setName(name);
			channel.setCode(code);
			channel.setUpdateTime(new Date());
			channelMapper.updateByPrimaryKeySelective(channel);
			String content = "修改了渠道信息,渠道名称:"+name;
			sysLogService.addSysLog(loginAccount, content, new Date());
		}
		return 1;
	}

	@Transactional
	public int delChannel(String channelId, Account loginAccount,Object apiResult) {
		ApiResult result = (ApiResult)apiResult;
		if(StringUtils.isBlank(channelId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的渠道");
			return 0;
		}
		Channel channel = new Channel();
		channel.setId(Integer.valueOf(channelId));
		channel.setIsDel((byte)1);
		channel.setUpdateTime(new Date());
		channelMapper.updateByPrimaryKeySelective(channel);
		String content = "删除了一个渠道";
		sysLogService.addSysLog(loginAccount, content, new Date());
		return 1;
	}

	public List<Channel> getChannelList(Long accountId,String keyword, Object apiResult) {
		List<Channel> list = null;
		if(accountId!=null) {
			Map<String, Object> map = new HashMap<String, Object>();
			if(StringUtils.isNotBlank(keyword)){
				keyword = keyword.replaceAll("\\s*", "");
				map.put("keyword", "%"+keyword+"%");
			}
			list = channelMapper.selectByMap(map);
		}
		return list;
	}
	
	@Transactional
	public int saveHospital(String jsonStr,Account loginAccount, Object apiResult) {
		if(jsonStr!=null) {
			ApiResult result = (ApiResult)apiResult;
			HospitalBo hospitalBo = JSONObject.parseObject(jsonStr, HospitalBo.class);
			if(hospitalBo.getId()==null) {
				//新增
				int checkParam = checkParam(hospitalBo, result,1);
				if(checkParam==0) return 0;
				Hospital hospital = new Hospital();
				BeanUtils.copyProperties(hospitalBo, hospital);
				hospital.setCreatorId(loginAccount.getId());
				hospital.setCreateTime(new Date());
				//编号转大写
				hospital.setHospitalCode(hospital.getHospitalCode().toUpperCase());
				//门店/机构
				hospitalMapper.insertSelective(hospital);
				List<HospitalLinkman> linkManList = hospitalBo.getLinkManList();
				for (HospitalLinkman linkMan : linkManList) {
					//门店联系人
					linkMan.setId(null);
					linkMan.setHospitalId(hospital.getId());
					hospitalLinkmanMapper.insertSelective(linkMan);
				}
				String content = "新增了一个机构信息,机构名称:"+hospitalBo.getHospitalName();
				sysLogService.addSysLog(loginAccount, content, new Date());
			}else {
				//修改
				int checkParam = checkParam(hospitalBo, result,2);
				if(checkParam==0) return 0;
				Hospital hospital = new Hospital();
				BeanUtils.copyProperties(hospitalBo, hospital);
				hospital.setUpdateTime(new Date());
				//编号转大写
				hospital.setHospitalCode(hospital.getHospitalCode().toUpperCase());
				//门店/机构
				hospitalMapper.updateByPrimaryKeySelective(hospital);
				
				List<HospitalLinkman> oldLinkManList = hospitalLinkmanMapper.selectByHospitalId(hospital.getId());
				//先删除原来的数据
				for (HospitalLinkman linkMan : oldLinkManList) {
					hospitalLinkmanMapper.deleteByPrimaryKey(linkMan.getId());
				}
				//保存最新
				List<HospitalLinkman> linkManList = hospitalBo.getLinkManList();
				for (HospitalLinkman linkMan : linkManList) {
					linkMan.setId(null);
					linkMan.setHospitalId(hospital.getId());
					hospitalLinkmanMapper.insertSelective(linkMan);
				}
				String content = "修改了机构信息,机构名称:"+hospitalBo.getHospitalName();
				sysLogService.addSysLog(loginAccount, content, new Date());
			}
		}
		return 1;
	}

	/**
	 * 校验新增或编辑门店信息  参数是否合法
	 * @param hospitalBo
	 * @param result
	 * @param type 1:新增 2:修改
	 * @return 0:不合法 1:合法
	 */
	private int checkParam(HospitalBo hospitalBo,ApiResult result,int type) {
		
		if(StringUtils.isBlank(hospitalBo.getHospitalName())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入机构名称");
			return 0;
		}
		if(StringUtils.isBlank(hospitalBo.getProvinceCode())||StringUtils.isBlank(hospitalBo.getCityCode())
				||StringUtils.isBlank(hospitalBo.getCountyCode())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择机构地址");
			return 0;
		}
		if(StringUtils.isBlank(hospitalBo.getDetailAddress())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入机构具体地址");
			return 0;
		}
		List<HospitalLinkman> linkManList = hospitalBo.getLinkManList();
		if(linkManList==null||linkManList.size()==0) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请添加机构联系人和联系方式");
			return 0;
		}else {
			for (HospitalLinkman linkMan : linkManList) {
				if(StringUtils.isBlank(linkMan.getLinkName())) {
					result.setCode(BizCode.PARAM_EMPTY);
					result.setMessage("请输入联系人姓名");
					return 0;
				}
				if(StringUtils.isBlank(linkMan.getLinkPhone())) {
					result.setCode(BizCode.PARAM_EMPTY);
					result.setMessage("请输入联系人电话");
					return 0;
				}
			}
		}
		if(StringUtils.isBlank(hospitalBo.getHospitalCode())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入机构编号");
			return 0;
		}
		if(type==1) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("hospitalCode", hospitalBo.getHospitalCode());
			List<Hospital> list = hospitalMapper.selectListByMap(map);
			if(list!=null && list.size()>0) {
				result.setCode(BizCode.DATA_EXIST);
				result.setMessage("机构编号【"+hospitalBo.getHospitalCode()+"】已存在,请重新输入");
				return 0;
			}
		}
		if(StringUtils.isBlank(hospitalBo.getChannelId())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择渠道");
			return 0;
		}
		if(StringUtils.isBlank(hospitalBo.getCheckItemIds())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择可检测项目");
			return 0;
		}
		if(hospitalBo.getIsHospital()==null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择该门店是否是医院");
			return 0;
		}
		if(hospitalBo.getReportNeedAduit()==null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择该门店采样报告是否需要审核");
			return 0;
		}
		return 1;
	}

	public JSONObject getHospitalList(String keyword, Integer channelId, Integer pageNo, Integer pageSize, Long accountId,Object apiResult) {
		JSONObject json = new JSONObject();
		Map<String,Object> map = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", "");//去除所有空格 
			map.put("keyword", "%"+keyword+"%");//编号,机构名
		}
		if(channelId!=null) {
			map.put("channelId", channelId);
		}
		map.put("isDel", 0);
		Integer totalCount = hospitalMapper.selectCountByMap(map);
		if(totalCount==null||totalCount==0) {
			json.put("page", null);
			json.put("hospitalList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<Hospital> list = hospitalMapper.selectListByMap(map);
		for (Hospital hospital : list) {
			//可检测项目
			String checkItemName = "";
			String checkItemIds = hospital.getCheckItemIds();
			if(checkItemIds!=null) {
				int i = 0;
				String[] arr = checkItemIds.split("\\,");
				for (String itemId : arr) {
					CheckItem checkItem = checkItemMapper.selectByPrimaryKey(Integer.valueOf(itemId));
					if(checkItem!=null) {
						if(i==0) {
							checkItemName = checkItemName + checkItem.getName();
						}else {
							checkItemName = checkItemName + " "+ checkItem.getName();
						}
						i++;
					}
				}
			}
			hospital.setCheckItemNames(checkItemName);
			//渠道名称
			Channel channel = channelMapper.selectByPrimaryKey(hospital.getChannelId());
			hospital.setChannelName(channel==null?"":channel.getName());
		}
		json.put("page", page);
		json.put("hospitalList", list);
		return json;
	}

	@Transactional
	public int delHospital(String hospitalId, Account loginAccount,Object apiResult) {
		ApiResult result = (ApiResult)apiResult;
		if(StringUtils.isBlank(hospitalId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的门店");
			return 0;
		}
		//查询门店是否被账号,科室,医生等数据占用,若存在不允许删除
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isDel", 0);
		map.put("hospitalId", hospitalId);
		Integer count = accountMapper.selectCountByMap(map);
		if(count==null || count==0) {
			count = hospitalRoomMapper.selectCountByMap(map);
			if(count!=null && count>0) {
				result.setCode(BizCode.NOT_ALLOW_DEL);
				result.setMessage("该门店不可删除,有账号在使用该门店");
				return 0;
			}
		}
		Hospital record = new Hospital();
		record.setId(Long.valueOf(hospitalId));
		record.setIsDel((byte)1);
		record.setUpdateTime(new Date());
		hospitalMapper.updateByPrimaryKeySelective(record);
		String content = "删除了一个机构信息";
		sysLogService.addSysLog(loginAccount, content, new Date());
		return 1;
	}

	@Transactional
	public int saveHospitalRoom(String jsonstr, Account loginAccount, Object apiResult) {
		if(jsonstr !=null) {
			ApiResult result = (ApiResult)apiResult;
			HospitalRoom room = JSONObject.parseObject(jsonstr, HospitalRoom.class);
			if(StringUtils.isBlank(room.getRoomName())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请输入科室名称");
				return 0;
			}
			if(StringUtils.isBlank(room.getRoomCode())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请输入科室编号");
				return 0;
			}
			if(StringUtils.isBlank(room.getHospitalId())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择机构名称");
				return 0;
			}
			if(room.getId()==null) {//新增
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("roomName", room.getRoomName());
				List<HospitalRoom> list = hospitalRoomMapper.selectListByMap(map);
				if(list!=null && list.size()>0) {
					result.setCode(BizCode.DATA_EXIST);
					result.setMessage("科室名称【"+room.getRoomName()+"】已存在,请重新输入");
					return 0;
				}
				map.remove("roomName");
				map.put("roomCode", room.getRoomCode());
				List<HospitalRoom> list2 = hospitalRoomMapper.selectListByMap(map);
				if(list2!=null && list2.size()>0) {
					result.setCode(BizCode.DATA_EXIST);
					result.setMessage("科室编号【"+room.getRoomCode()+"】已存在,请重新输入");
					return 0;
				}
				room.setCreatorId(loginAccount.getId());
				room.setCreateTime(new Date());
				hospitalRoomMapper.insertSelective(room);
				String content = "新增了一个科室,名称:"+room.getRoomName();
				sysLogService.addSysLog(loginAccount, content, new Date());
			}else {//修改
				room.setUpdateTime(new Date());
				hospitalRoomMapper.updateByPrimaryKeySelective(room);
				String content = "修改了科室信息,名称:"+room.getRoomName();
				sysLogService.addSysLog(loginAccount, content, new Date());
			}
		}
		return 1;
	}

	@Transactional
	public int delHospitalRoom(String roomId,Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult)apiResult;
		if(StringUtils.isBlank(roomId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的科室");
			return 0;
		}
		//查询门店是否被医生数据占用,若存在不允许删除
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isDel", 0);
		map.put("hospitalRoomId", roomId);
		Integer count = hospitalDoctorMapper.selectCountByMap(map);
		if(count!=null && count>0) {
			result.setCode(BizCode.NOT_ALLOW_DEL);
			result.setMessage("该科室不可删除");
			return 0;
		}
		HospitalRoom room = new HospitalRoom();
		room.setId(Long.valueOf(roomId));
		room.setUpdateTime(new Date());
		room.setIsDel((byte)1);
		hospitalRoomMapper.updateByPrimaryKeySelective(room);
		String content = "删除了一个科室";
		sysLogService.addSysLog(loginAccount, content, new Date());
		return 1;
	}

	public JSONObject getHospitalRoomList(String keyword, Integer hospitalId, Integer pageNo, Integer pageSize,Account account) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", "");
			map.put("keyword", "%"+keyword+"%");
		}
		if(hospitalId!=null) {
			map.put("hospitalId", hospitalId);
		}else {
			//科室列表查询规则: 非超级管理员需按照门店id进行查询
			if(account!=null && account.getAccountType()!=AccountType.SUPERADMIN.getValue()) {
				map.put("hospitalId", account.getHospitalId());
			}
		}
		map.put("isDel", 0);
		Integer totalCount = hospitalRoomMapper.selectCountByMap(map);
		JSONObject json = new JSONObject();
		if(totalCount==null||totalCount==0) {
			json.put("page", null);
			json.put("roomList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<HospitalRoom> list = hospitalRoomMapper.selectListByMap(map);
		List<JSONObject> roomList = new ArrayList<JSONObject>();
		for (HospitalRoom hospitalRoom : list) {
			JSONObject room = new JSONObject();
			room.put("id", hospitalRoom.getId());
			room.put("roomName", hospitalRoom.getRoomName());
			room.put("roomCode", hospitalRoom.getRoomCode());
			room.put("hospitalId", hospitalRoom.getHospitalId());
			Hospital hospital = hospitalMapper.selectByPrimaryKey(hospitalRoom.getHospitalId());
			room.put("hospitalName", hospital==null?"":hospital.getHospitalName());
			roomList.add(room);
		}
		json.put("page", page);
		json.put("roomList", roomList);
		return json;
	}

	@Transactional
	public int saveHospitalDoctor(String jsonstr, Account loginAccount, Object apiResult) {
		if(jsonstr !=null) {
			ApiResult result = (ApiResult)apiResult;
			HospitalDoctor doctor = JSONObject.parseObject(jsonstr, HospitalDoctor.class);
			if(StringUtils.isBlank(doctor.getDoctorName())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请输入医生名称");
				return 0;
			}
			if(StringUtils.isBlank(doctor.getDoctorCode())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请输入医生编号");
				return 0;
			}
			if(StringUtils.isBlank(doctor.getHospitalId())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择机构");
				return 0;
			}
			if(StringUtils.isBlank(doctor.getHospitalRoomId())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择科室");
				return 0;
			}
			String content = "";
			if(doctor.getId()==null) {//新增
				doctor.setCreatorId(loginAccount.getId());
				doctor.setCreateTime(new Date());
				hospitalDoctorMapper.insertSelective(doctor);
				content = "新增了一个医生,姓名："+doctor.getDoctorName();
			}else {//修改
				doctor.setUpdateTime(new Date());
				hospitalDoctorMapper.updateByPrimaryKeySelective(doctor);
				content = "修改了医生信息,姓名："+doctor.getDoctorName();
			}
			sysLogService.addSysLog(loginAccount, content, new Date());
		}
		return 1;
	}

	@Transactional
	public int delHospitalDoctor(String doctorId, Account loginAccount,Object apiResult) {
		ApiResult result = (ApiResult)apiResult;
		if(StringUtils.isBlank(doctorId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的医生");
			return 0;
		}
		HospitalDoctor doctor = new HospitalDoctor();
		doctor.setId(Long.valueOf(doctorId));
		doctor.setUpdateTime(new Date());
		doctor.setIsDel((byte)1);
		hospitalDoctorMapper.updateByPrimaryKeySelective(doctor);
		String content = "删除了一个医生";
		sysLogService.addSysLog(loginAccount, content, new Date());
		return 1;
	}

	public JSONObject getHospitalDoctorList(String keyword, Integer pageNo,Integer pageSize, Account account) {
		Map<String,Object> map = new HashMap<String,Object>();
		//医生列表查询规则: 非超级管理员需按照门店id进行查询
		if(account!=null && account.getAccountType()!=AccountType.SUPERADMIN.getValue()) {
			map.put("hospitalId", account.getHospitalId());
		}
		if(StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", "");
			map.put("keyword", "%"+keyword+"%");
		}
		map.put("isDel", 0);
		Integer totalCount = hospitalDoctorMapper.selectCountByMap(map);
		JSONObject json = new JSONObject();
		if(totalCount==null||totalCount==0) {
			json.put("page", null);
			json.put("doctorList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<HospitalDoctor> list = hospitalDoctorMapper.selectListByMap(map);
		List<JSONObject> doctorList = new ArrayList<JSONObject>();
		for (HospitalDoctor hospitalDoctor : list) {
			JSONObject doctor = new JSONObject();
			doctor.put("id", hospitalDoctor.getId());
			doctor.put("doctorName", hospitalDoctor.getDoctorName());
			doctor.put("doctorCode", hospitalDoctor.getDoctorCode());
			Hospital hospital = hospitalMapper.selectByPrimaryKey(hospitalDoctor.getHospitalId());
			//机构名称
			doctor.put("hospitalName", hospital==null?"":hospital.getHospitalName());
			HospitalRoom hospitalRoom = hospitalRoomMapper.selectByPrimaryKey(hospitalDoctor.getHospitalRoomId());
			//科室名称
			doctor.put("roomName", hospitalRoom==null?"":hospitalRoom.getRoomName());
			doctor.put("hospitalId", hospitalDoctor.getHospitalId());
			doctor.put("hospitalRoomId", hospitalDoctor.getHospitalRoomId());
			doctorList.add(doctor);
		}
		json.put("page", page);
		json.put("doctorList", doctorList);
		return json;
	}

	public int saveHospitalGroup(String groupId,String name, String hospitalIds, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult)apiResult;
		if(StringUtils.isBlank(name)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入门店组名称");
			return 0;
		}
		if(StringUtils.isBlank(hospitalIds)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请至少选择一个门店");
			return 0;
		}
		HospitalGroup group = new HospitalGroup();
		group.setName(name);
		group.setHospitalIds(hospitalIds);
		String content = "";
		if(StringUtils.isBlank(groupId)) {//新增
			group.setCreateTime(new Date());
			group.setCreatorId(loginAccount.getCreatorId());
			hospitalGroupMapper.insertSelective(group);
			content = "新增了一个门店组";
		}else {//修改
			group.setId(Integer.valueOf(groupId));
			group.setUpdateTime(new Date());
			hospitalGroupMapper.updateByPrimaryKeySelective(group);
			content = "修改了门店组信息";
		}
		sysLogService.addSysLog(loginAccount, content, new Date());
		return 1;
	}

	public int delHospitalGroup(String groupId,Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult)apiResult;
		if(StringUtils.isBlank(groupId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的门店组");
			return 0;
		}
		//查询门店组是否被账号数据占用,若存在不允许删除
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isDel", 0);
		map.put("hospitalGroupId", groupId);
		Integer count = accountMapper.selectCountByMap(map);
		if(count!=null && count>0) {
			result.setCode(BizCode.NOT_ALLOW_DEL);
			result.setMessage("该门店组不可删除");
			return 0;
		}
		HospitalGroup group = new HospitalGroup();
		group.setId(Integer.valueOf(groupId));
		group.setUpdateTime(new Date());
		group.setIsDel((byte)1);
		hospitalGroupMapper.updateByPrimaryKeySelective(group);
		String content = "删除了一个门店组";
		sysLogService.addSysLog(loginAccount, content, new Date());
		return 1;
	}

	public JSONObject getHospitalGroupList(Integer pageNo, Integer pageSize, Account account) {
		JSONObject json = new JSONObject();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("isDel", 0);
		Integer totalCount = hospitalGroupMapper.selectCountByMap(map);
		if(totalCount==null||totalCount==0) {
			json.put("page", null);
			json.put("hospitalGroupList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<HospitalGroup> list = hospitalGroupMapper.selectListByMap(map);
		for (HospitalGroup group : list) {
			String hospitalNames = "";
			String hospitalIds = group.getHospitalIds();
			if(StringUtils.isNotBlank(hospitalIds)) {
				int i = 1;
				String[] arr = hospitalIds.split("\\,");
				for (String hospitalId : arr) {
					Hospital hospital = hospitalMapper.selectByPrimaryKey(Long.valueOf(hospitalId));
					if(hospital!=null) {
						if(i==1) {
							hospitalNames = hospitalNames + hospital.getHospitalName();
						}else {
							hospitalNames = hospitalNames + ","+ hospital.getHospitalName();
						}
					}
					i++;
				}
			}else {
				hospitalNames = "--";
			}
			group.setHospitalNames(hospitalNames);
		}
		json.put("page", page);
		json.put("hospitalGroupList", list);
		return json;
	}

	public JSONObject getHospitalDetail(String hospitalId, Account account, Object apiResult) {
		ApiResult result = (ApiResult)apiResult;
		if(StringUtils.isBlank(hospitalId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数hospitalId为空");
			return null;
		}
		JSONObject json = new JSONObject();
		Hospital hospital = hospitalMapper.selectByPrimaryKey(Long.valueOf(hospitalId));
		if(hospital!=null) {
			json.put("id", hospital.getId());
			json.put("hospitalName", hospital.getHospitalName());
			json.put("logoUrl", hospital.getLogoUrl());
			json.put("provinceCode", hospital.getProvinceCode());
			json.put("cityCode", hospital.getCityCode());
			json.put("countyCode", hospital.getCountyCode());
			json.put("detailAddress", hospital.getDetailAddress());
			json.put("areaCodeName", hospital.getAreaCodeName());
			json.put("hospitalCode", hospital.getHospitalCode());
			json.put("channelId", hospital.getChannelId());
			json.put("checkItemIds", hospital.getCheckItemIds());
			//门店联系人
			List<HospitalLinkman> linkManList = hospitalLinkmanMapper.selectByHospitalId(hospital.getId());
			json.put("linkManList", linkManList);
			//可检测项目
			String checkItemName = "";
			String checkItemIds = hospital.getCheckItemIds();
			if(checkItemIds!=null) {
				int i = 0;
				String[] arr = checkItemIds.split("\\,");
				for (String itemId : arr) {
					CheckItem checkItem = checkItemMapper.selectByPrimaryKey(Integer.valueOf(itemId));
					if(checkItem!=null) {
						if(i==0) {
							checkItemName = checkItemName + checkItem.getName();
						}else {
							checkItemName = checkItemName + " "+ checkItem.getName();
						}
						i++;
					}
				}
			}
			json.put("checkItemNames", checkItemName);
			//渠道名称
			Channel channel = channelMapper.selectByPrimaryKey(hospital.getChannelId());
			json.put("channelName", channel==null?"":channel.getName());
			json.put("isHospital", hospital.getIsHospital());
			json.put("reportNeedAduit", hospital.getReportNeedAduit());
		}
		return json;
	}
}
