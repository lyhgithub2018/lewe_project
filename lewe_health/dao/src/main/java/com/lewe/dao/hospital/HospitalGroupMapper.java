package com.lewe.dao.hospital;

import java.util.List;
import java.util.Map;

import com.lewe.bean.hospital.HospitalGroup;

public interface HospitalGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HospitalGroup record);

    int insertSelective(HospitalGroup record);

    HospitalGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HospitalGroup record);

    int updateByPrimaryKey(HospitalGroup record);

	Integer selectCountByMap(Map<String, Object> map);

	List<HospitalGroup> selectListByMap(Map<String, Object> map);
}