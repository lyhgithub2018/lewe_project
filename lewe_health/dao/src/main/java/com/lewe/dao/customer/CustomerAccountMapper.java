package com.lewe.dao.customer;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lewe.bean.check.CheckDevice;
import com.lewe.bean.customer.CustomerAccount;
import com.lewe.bean.customer.vo.CustomerFansInfo;

public interface CustomerAccountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustomerAccount record);

    int insertSelective(CustomerAccount record);

    CustomerAccount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustomerAccount record);

    int updateByPrimaryKey(CustomerAccount record);

    /**
     * 通过手机号查询客户的账号
     * @param phone
     * @return
     */
	CustomerAccount selectByPhone(@Param("phone")String phone);

	Integer selectCountByMap(Map<String, Object> map);

	List<CustomerFansInfo> selectListByMap(Map<String, Object> map);
}