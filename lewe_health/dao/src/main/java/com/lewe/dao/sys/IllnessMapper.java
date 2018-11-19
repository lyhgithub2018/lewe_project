package com.lewe.dao.sys;

import java.util.List;
import java.util.Map;

import com.lewe.bean.sys.Illness;

public interface IllnessMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Illness record);

    int insertSelective(Illness record);

    Illness selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Illness record);

    int updateByPrimaryKey(Illness record);

	Integer selectCountByMap(Map<String, Object> map);

	List<Illness> selectListByMap(Map<String, Object> map);
}