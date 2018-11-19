package com.lewe.dao.sys;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lewe.bean.sys.Account;

public interface AccountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Account record);

    int insertSelective(Account record);

    Account selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Account record);

    int updateByPrimaryKey(Account record);
    
    /**
     * 通过登录账号查询系统账号数据
     * @param map
     * @return
     */
	Account selectByAccount(@Param("account")String account);

	Integer selectCountByMap(Map<String, Object> map);

	List<Account> selectListByMap(Map<String, Object> map);
}