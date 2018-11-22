package com.lewe.service.check;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;

public interface ICheckManageService {

	/**
	 * 检测管理-保存检测项目
	 * @param checkItem
	 * @param account
	 * @param ApiResult
	 * @return
	 */
	public int saveCheckItem(String checkItem, Account account, Object apiResult);

	/**
	 * 检测管理-删除检测项目
	 * @param itemId
	 * @param account
	 * @param apiResult
	 * @return
	 */
	public int delCheckItem(String itemId, Account account, Object apiResult);

	/**
	 * 检测管理-检测项目列表
	 * @param keyword 
	 * @param pageSize 
	 * @param pageNo 
	 * @param account
	 * @param result
	 * @return
	 */
	public JSONObject getCheckItemList(String keyword, Integer pageNo, Integer pageSize, Account account, Object apiResult);

	/**
	 * 检测管理-检测样品列表查询
	 * @param sampleInfoQuery
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public JSONObject checkSampleList(String sampleInfoQuery, Account loginAccount, Object apiResult);

	/**
	 * 检测管理-获取检测样品详情
	 * @param id
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public JSONObject checkInfoDetail(Long id,String sampleCode, Account loginAccount, Object apiResult);

	/**
	 * 检测管理-提交检测信息
	 * @param checkInfo
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public int submitCheckInfo(String checkInfo, Account loginAccount, Object apiResult);

	/**
	 * 检测管理-保存检测设备
	 * @param id
	 * @param name
	 * @param code
	 * @param hospitalId
	 * @param account
	 * @param apiResult
	 * @return
	 */
	public int saveCheckDevice(Integer id, String name, String code, Long hospitalId, Account account, Object apiResult);

	/**
	 * 删除检测设备
	 * @param deviceId
	 * @param account
	 * @param apiResult
	 * @return
	 */
	public int delCheckDevice(Integer deviceId, Account account, Object apiResult);

	/**
	 * 检测设备列表
	 * @param hospitalId
	 * @param pageNo
	 * @param pageSize
	 * @param account
	 * @param apiResult
	 * @return
	 */
	public JSONObject getCheckDeviceList(Long hospitalId, Integer pageNo, Integer pageSize, Account account, Object apiResult);

	/**
	 * 检测审核列表
	 * @param sampleInfoQuery
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public JSONObject checkAuditList(String sampleInfoQuery, Account loginAccount, Object apiResult);

	/**
	 * 提交审核信息(对应页面'审核通过'操作)
	 * @param auditInfo
	 * @param operateType 1:审核通过  2:待修改
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public int submitAuditInfo(String auditInfo, Integer operateType,Account loginAccount, Object apiResult);

	/**
	 * 获取审核信息详情
	 * @param id
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public JSONObject auditInfoDetail(Long id, Account loginAccount, Object apiResult);

	/**
	 * 待修改操作
	 * 注:将审核状态更改为待修改
	 */
	public int updateAuditStatus(Long id, Account loginAccount, Object apiResult);

	/**
	 * 获取检测项目对应的一组底物和剂量
	 * @param itemId
	 * @param account
	 * @param result
	 * @return
	 */
	public JSONObject getItemSubstrateList(Integer itemId, Account account, Object result);

	/**
	 * 校验某个项目的底物是否在使用中
	 * @param substrateId
	 * @param account
	 * @param result
	 * @return 0:否  1:是
	 */
	public JSONObject checkSubstrateIsUsed(Integer itemId, Account account, Object result);

	/**
	 * 获取检测页面或审核页面或报告页面中需要展示的字段
	 * @param reportId 报告信息主键id(C端报告详情中传该参数)
	 * @return
	 */
	public JSONObject getShowFieldList(Long reportId, Account loginAccount, Object result);
}
