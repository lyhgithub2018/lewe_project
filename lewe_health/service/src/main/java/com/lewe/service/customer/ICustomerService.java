package com.lewe.service.customer;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.customer.CustomerAccount;
import com.lewe.bean.sys.Account;

public interface ICustomerService {

	/**
	 * C端客户绑定采样报告编号
	 * @param customerId
	 * @param sampleCode
	 * @param apiResult
	 * @return
	 */
	public Boolean bindSampleCode(String customerId, String sampleCode, Object apiResult);
	/**
	 * C端客户提交问卷信息
	 * @param questionnaire1
	 * @param questionnaire2
	 * @param apiResult
	 * @return
	 */
	public int submitQuestionnaire(String questionnaire1, String questionnaire2,String sampleCode, Object apiResult);
	/**
	 * 发送短信验证码
	 * @param phone
	 * @param apiResult
	 * @return
	 */
	public String sendSmsCode(String phone, Object apiResult);
	/**
	 * C端客户注册登录接口
	 * @param phone
	 * @param smsCode
	 * @param smsCode
	 * @param apiResult
	 * @return
	 */
	public CustomerAccount registOrLogin(String phone, String smsCode,Long fansId, Object apiResult);
	/**
	 * C端用户管理列表
	 * @param keyword
	 * @param status
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public JSONObject getUserManageList(String keyword, Byte status, Integer pageNo, Integer pageSize);
	/**
	 * C端用户管理--修改手机号
	 * @param id
	 * @param phone
	 * @param account
	 * @return
	 */
	public int updatePhone(Long id, String phone, Account account,Object apiResult);
	/**
	 * C端用户管理列表--冻结/解冻账号
	 * @param id
	 * @param status
	 * @param account
	 * @param result
	 * @return
	 */
	public int freezeCustomer(Long id, Byte status, Account account,Object apiResult);
	/**
	 * C端用户--我的报告列表
	 * @param customerId
	 * @param keyword
	 * @param beginDate
	 * @param endDate
	 * @param apiResult
	 * @return
	 */
	public JSONObject myReportList(Long customerId, String keyword, String beginDate, String endDate, Object apiResult);
	/**
	 * C端用户--我的报告详情
	 * @param reportId
	 * @param apiResult
	 * @return
	 */
	public JSONObject myReportInfo(Long reportId, Object apiResult);
	/**
	 * 通过fansId查询C端客户账号
	 * @param fansId
	 * @return
	 */
	public CustomerAccount getCustomerAccountByFansId(Long fansId);
	/**
	 * C端用户--获取问卷信息1(相当于完善信息时的获取)
	 * @param reportId
	 * @param apiResult
	 * @return
	 */
	public JSONObject getQuestionnaire1Info(Long reportId, String sampleCode,Object apiResult);
	
	public JSONObject getFansInfo(Long fansId,Long customerId, Object apiResult);

}
