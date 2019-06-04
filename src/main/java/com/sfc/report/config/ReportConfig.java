package com.sfc.report.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Data
//@ConfigurationProperties(prefix = "report")
public class ReportConfig {
    /**
     * SQL文件路径
     */
    @Value("${report.sqlpath}")
    private String sqlpath;
    /**
     * 文件路径
     */
    @Value("${report.filepath}")
    private String filepath;
    /**
     * 产品代码
     */
    @Value("${report.product}")
    private String product;
    /**
     * 开始日期
     */
    @Value("${report.beginDate}")
    private String beginDate;
    /**
     * 结束日期
     */
    @Value("${report.endDate}")
    private String endDate;
    /**
     * 文件名称
     */
    @Value("${report.filename}")
    private String filename;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;



}
