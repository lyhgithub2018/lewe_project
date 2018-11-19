package com.lewe.dao.report;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lewe.bean.customer.query.CustomerInfoQuery;
import com.lewe.bean.customer.query.SampleInfoQuery;
import com.lewe.bean.report.ReportInfo;

public interface ReportInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ReportInfo record);

    int insertSelective(ReportInfo record);

    ReportInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReportInfo record);

    int updateByPrimaryKey(ReportInfo record);

	ReportInfo selectBySampleCode(@Param("sampleCode")String sampleCode);

	/**
	 * 客户管理-客户信息列表查询
	 * @param query
	 * @return
	 */
	List<ReportInfo> selectListByCustomerInfoQuery(CustomerInfoQuery query);

	/**
	 * 客户管理-客户信息列表count
	 * @param query
	 * @return
	 */
	Integer selectCountByCustomerInfoQuery(CustomerInfoQuery query);

	/**
	 * 检测管理-检测样品列表count
	 * @param map
	 * @return
	 */
	Integer selectCountBySampleInfoQuery(SampleInfoQuery query);

	/**
	 * 检测管理-检测样品列表查询
	 * @param map
	 * @return
	 */
	List<ReportInfo> selectListBySampleInfoQuery(SampleInfoQuery query);

	List<ReportInfo> selectListByMap(Map<String, Object> paramMap);

	Integer selectCountByMap(Map<String, Object> paramMap);

	int updateCustomerPhoneByCustomerId(Map<String, Object> map);
}