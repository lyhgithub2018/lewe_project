package com.lewe.dao.hospital;

import java.util.List;
import java.util.Map;

import com.lewe.bean.hospital.HospitalRoom;

public interface HospitalRoomMapper {
    int deleteByPrimaryKey(Long id);

    int insert(HospitalRoom record);

    int insertSelective(HospitalRoom record);

    HospitalRoom selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HospitalRoom record);

    int updateByPrimaryKey(HospitalRoom record);

	List<HospitalRoom> selectListByMap(Map<String, Object> map);

	Integer selectCountByMap(Map<String, Object> map);
}