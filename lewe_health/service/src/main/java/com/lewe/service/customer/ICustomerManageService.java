package com.lewe.service.customer;


import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.customer.vo.CustomerInfoVo;
import com.lewe.bean.sys.Account;

/**
 * B端客户管理相关的service
 * @author 小辉
 *
 */
public interface ICustomerManageService {

	/**
	 * B端客户管理 客户信息列表
	 * @param customerInfoQuery 查询条件对象
	 * @param loginAccount 当前登录账号
	 * @param apiResult 
	 * @return
	 */
	public JSONObject getCustomerInfoList(String customerInfoQuery, Account loginAccount, Object apiResult);

	/**
	 * 新增或修改客户信息
	 * @param operateType 操作类型 1:新增 2:修改
	 * @param customerInfo
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public int addOrUpdateCustomerInfo(Integer operateType,String customerInfo, Account loginAccount, Object apiResult);

	/**
	 * 客户管理--获取客户信息详情
	 * @param customerInfoId
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public CustomerInfoVo getCustomerInfo(Long customerInfoId, Account loginAccount, Object apiResult);

	/**
	 * 检测当前编号是否已经扫描过
	 * @param scanCode 扫描测出来的采样者编号
	 * @param loginAccount
	 * @param apiResult
	 * @return 1:已扫描过 2:未扫描过 3:编号无对应客户
	 */
	public int checkIsHadScan(String scanCode, Account loginAccount, Object apiResult);

	/**
	 * 确定提交扫描出来的编号
	 * @param codeArray
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public JSONObject submitScanCodes(String codeArray, Account loginAccount, Object apiResult);

	/**
	 * 快递信息列表查询
	 * @param keyword
	 * @param channelId
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public JSONObject expressInfoList(String keyword, Integer channelId,Integer pageNo,Integer pageSize, Account loginAccount, Object apiResult);

	/**
	 * 新增快递信息
	 * @param expressName
	 * @param expressCode
	 * @param customerInfoIds
	 * @param channelId
	 * @param hospitalId
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public int addExpressInfo(String expressName, String expressCode, String customerInfoIds, Integer channelId,
			Long hospitalId, Account loginAccount, Object apiResult);

	/**
	 * 快递信息详情
	 * @param id
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public JSONObject expressInfoDetail(Long id, Account loginAccount, Object apiResult);

	/**
	 * 新增快递时选择客户下拉列表
	 * 注:只查询当前门店下已经扫描过且未绑定过快递单号的客户
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	public List<JSONObject> customerSelectList(Account loginAccount, Object apiResult);

}
