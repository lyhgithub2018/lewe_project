package com.lewe.dao.report;

import java.util.List;
import java.util.Map;

import com.lewe.bean.report.ReportIllness;

public interface ReportIllnessMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReportIllness record);

    int insertSelective(ReportIllness record);

    ReportIllness selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ReportIllness record);

    int updateByPrimaryKey(ReportIllness record);

	List<ReportIllness> selectListByMap(Map<String, Object> map);

	int deleteByReportInfoId(Long id);
}