package com.lewe.service.hospital;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.Channel;

public interface IHospitalService {
	
	/**
	 * 新增或修改 系统渠道数据
	 * @param name
	 * @param code
	 * @param channelId
	 * @param apiResult
	 * @return
	 */
	public int saveChannel(String name, String code, String channelId,Account loginAccount, Object apiResult);

	/**
	 * 删除渠道
	 * @param channelId
	 * @param apiResult
	 * @return
	 */
	public int delChannel(String channelId, Account loginAccount,Object apiResult);

	/**
	 * 渠道列表
	 * @param channelId
	 * @param keyword
	 * @param apiResult
	 * @return
	 */
	
	public List<Channel> getChannelList(Long accountId,String keyword, Object apiResult);
	/**
	 * 保存或修改 门店/机构
	 * @param jsonStr
	 * @param apiResult
	 * @return
	 */
	public int saveHospital(String jsonStr,Account loginAccount,Object apiResult);

	/**
	 * 分页查询当前账号下的门店/机构列表
	 * @param keyword
	 * @param channelId
	 * @param pageNo
	 * @param pageSize
	 * @param accountId
	 * @param apiResult
	 * @return
	 */
	public JSONObject getHospitalList(String keyword, Integer channelId, Integer pageNo, Integer pageSize, Long accountId,Object apiResult);

	/**
	 * 删除门店
	 * @param hospitalId
	 * @param apiResult
	 * @return
	 */
	public int delHospital(String hospitalId,Account loginAccount,Object apiResult);

	/**
	 * 新增或修改科室
	 * @param jsonstr
	 * @param accountId
	 * @param apiResult
	 * @return
	 */
	public int saveHospitalRoom(String jsonstr, Account loginAccount, Object apiResult);

	/**
	 * 删除科室
	 * @param roomId
	 * @param apiResult
	 * @return
	 */
	public int delHospitalRoom(String roomId, Account loginAccount,Object apiResult);

	/**
	 * 查询科室列表
	 * @param keyword
	 * @param hospitalId
	 * @param pageNo
	 * @param pageSize
	 * @param account
	 * @return
	 */
	public JSONObject getHospitalRoomList(String keyword, Integer hospitalId, Integer pageNo, Integer pageSize,Account account);

	/**
	 * 新增或修改医生
	 * @param hospitalDoctorStr
	 * @param accountId
	 * @param apiResult
	 * @return
	 */
	public int saveHospitalDoctor(String jsonstr, Account loginAccount, Object apiResult);

	/**
	 * 删除医生
	 * @param doctorId
	 * @param apiResult
	 * @return
	 */
	public int delHospitalDoctor(String doctorId, Account loginAccount,Object apiResult);

	/**
	 * 分页查询医生列表
	 * @param keyword
	 * @param pageNo
	 * @param pageSize
	 * @param account
	 * @return
	 */
	public JSONObject getHospitalDoctorList(String keyword, Integer pageNo, Integer pageSize, Account account);

	/**
	 * 新增或修改门店组
	 * @param name
	 * @param groupId
	 * @param hospitalIds
	 * @param accountId
	 * @param apiResult
	 * @return
	 */
	public int saveHospitalGroup(String groupId,String name, String hospitalIds, Account loginAccount, Object apiResult);

	/**
	 * 删除门店组
	 * @param groupId
	 * @param apiResult
	 * @return
	 */
	public int delHospitalGroup(String groupId, Account loginAccount,Object apiResult);

	/**
	 * 分页查询门店组列表
	 * @param pageNo
	 * @param pageSize
	 * @param account
	 * @return
	 */
	public JSONObject getHospitalGroupList(Integer pageNo, Integer pageSize, Account account);

	/**
	 * 查询门店机构详情
	 * @param hospitalId
	 * @param account
	 * @param apiResult
	 * @return
	 */
	public JSONObject getHospitalDetail(String hospitalId, Account account, Object apiResult);

}
