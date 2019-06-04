package com.sfc.report;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sfc.report.mapper")
public class ReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }

}
