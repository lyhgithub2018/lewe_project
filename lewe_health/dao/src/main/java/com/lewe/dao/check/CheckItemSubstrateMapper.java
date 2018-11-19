package com.lewe.dao.check;

import java.util.List;

import com.lewe.bean.check.CheckItemSubstrate;

public interface CheckItemSubstrateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CheckItemSubstrate record);

    int insertSelective(CheckItemSubstrate record);

    CheckItemSubstrate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CheckItemSubstrate record);

    int updateByPrimaryKey(CheckItemSubstrate record);

	int deleteByItemId(Integer id);

	List<CheckItemSubstrate> selectListByItemId(Integer id);
}