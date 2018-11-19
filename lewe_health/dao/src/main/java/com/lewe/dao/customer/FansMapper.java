package com.lewe.dao.customer;

import org.apache.ibatis.annotations.Param;

import com.lewe.bean.customer.Fans;

public interface FansMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Fans record);

    int insertSelective(Fans record);

    Fans selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Fans record);

    int updateByPrimaryKey(Fans record);

	Fans selectByOpenId(@Param("openId")String openId);
}