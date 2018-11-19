package com.lewe.dao.hospital;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lewe.bean.hospital.Hospital;

public interface HospitalMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Hospital record);

    int insertSelective(Hospital record);

    Hospital selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Hospital record);

    int updateByPrimaryKey(Hospital record);

	Integer selectCountByMap(Map<String, Object> map);

	List<Hospital> selectListByMap(Map<String, Object> map);

	/**
	 * 通过门店编号查询门店机构
	 * @param hospitalCode
	 * @return
	 */
	Hospital selectByHospitalCode(@Param("hospitalCode")String hospitalCode);
}