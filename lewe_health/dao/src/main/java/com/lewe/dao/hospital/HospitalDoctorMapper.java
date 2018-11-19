package com.lewe.dao.hospital;

import java.util.List;
import java.util.Map;

import com.lewe.bean.hospital.HospitalDoctor;

public interface HospitalDoctorMapper {
    int deleteByPrimaryKey(Long id);

    int insert(HospitalDoctor record);

    int insertSelective(HospitalDoctor record);

    HospitalDoctor selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HospitalDoctor record);

    int updateByPrimaryKey(HospitalDoctor record);
    
    Integer selectCountByMap(Map<String, Object> map);

	List<HospitalDoctor> selectListByMap(Map<String, Object> map);
}