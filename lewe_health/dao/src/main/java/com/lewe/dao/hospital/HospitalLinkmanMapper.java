package com.lewe.dao.hospital;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lewe.bean.hospital.HospitalLinkman;

public interface HospitalLinkmanMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HospitalLinkman record);

    int insertSelective(HospitalLinkman record);

    HospitalLinkman selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HospitalLinkman record);

    int updateByPrimaryKey(HospitalLinkman record);

	List<HospitalLinkman> selectByHospitalId(@Param("hospitalId")Long hospitalId);
}