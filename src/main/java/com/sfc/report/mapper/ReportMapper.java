package com.sfc.report.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface ReportMapper {

    /**
     * 执行SQL语句
     *
     * @param sql sql语句
     * @return
     */
    List<Map<String, Object>> generate(@Param("reportsql") String sql);
}