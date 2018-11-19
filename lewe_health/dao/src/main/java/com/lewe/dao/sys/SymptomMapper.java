package com.lewe.dao.sys;

import java.util.List;
import java.util.Map;

import com.lewe.bean.sys.Symptom;

public interface SymptomMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Symptom record);

    int insertSelective(Symptom record);

    Symptom selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Symptom record);

    int updateByPrimaryKey(Symptom record);

	Integer selectCountByMap(Map<String, Object> map);

	List<Symptom> selectListByMap(Map<String, Object> map);
}