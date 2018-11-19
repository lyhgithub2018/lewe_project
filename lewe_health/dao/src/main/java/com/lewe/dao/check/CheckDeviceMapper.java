package com.lewe.dao.check;

import java.util.List;
import java.util.Map;

import com.lewe.bean.check.CheckDevice;

public interface CheckDeviceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CheckDevice record);

    int insertSelective(CheckDevice record);

    CheckDevice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CheckDevice record);

    int updateByPrimaryKey(CheckDevice record);

	Integer selectCountByMap(Map<String, Object> map);

	List<CheckDevice> selectListByMap(Map<String, Object> map);
}