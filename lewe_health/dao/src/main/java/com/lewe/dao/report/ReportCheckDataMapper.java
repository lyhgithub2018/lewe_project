package com.lewe.dao.report;

import java.util.List;
import java.util.Map;

import com.lewe.bean.report.ReportCheckData;

public interface ReportCheckDataMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ReportCheckData record);

    int insertSelective(ReportCheckData record);

    ReportCheckData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReportCheckData record);

    int updateByPrimaryKey(ReportCheckData record);

	List<ReportCheckData> selectListByMap(Map<String, Object> map);

	int deleteByReportInfoId(Long id);
}