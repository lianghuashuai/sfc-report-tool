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
