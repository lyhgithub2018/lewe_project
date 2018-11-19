package com.lewe.dao.sys;

import com.lewe.bean.sys.SysFile;

public interface SysFileMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SysFile record);

    int insertSelective(SysFile record);

    SysFile selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysFile record);

    int updateByPrimaryKey(SysFile record);
}