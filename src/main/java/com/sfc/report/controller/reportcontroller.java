package com.sfc.report.controller;

import com.sfc.report.config.ReportConfig;
import com.sfc.report.service.generateservice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api("报表控制类")
@Slf4j
@Controller
@RequestMapping(path = "/report")
public class reportcontroller {

   @Autowired
   private generateservice sfcservice;
    @Autowired
    private ReportConfig reportConfig;




    @ApiOperation(value = "报表生成", notes = "参数为产品品种代码，用逗号分隔")
    @GetMapping(path = "/generate")
    public void generate(@ApiParam(value = "产品品种(多个用逗号分隔)") @RequestParam(value = "product") String product,
                                 @ApiParam(value = "起始日期", example = "20190101") @RequestParam(value = "beginDate") String beginDate,
                                 @ApiParam(value = "截至日期", example = "20190131") @RequestParam(value = "endDate") String endDate,
                                 HttpServletResponse response){
         String products=sfcservice.productParam(product.trim());
         sfcservice.reportservice(products,beginDate,endDate,response);

    }

    @ApiOperation(value = "工具参数页面", notes = "参数为产品品种代码，用逗号分隔")
    @GetMapping(path = "/tool")
    public String tool(){
        return "index";
    }
}
