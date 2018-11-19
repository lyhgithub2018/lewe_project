package com.lewe.dao.sys;

import com.lewe.bean.sys.ShowField;

public interface ShowFieldMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShowField record);

    int insertSelective(ShowField record);

    ShowField selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShowField record);

    int updateByPrimaryKey(ShowField record);
}