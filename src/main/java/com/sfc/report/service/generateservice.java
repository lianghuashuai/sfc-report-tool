package com.sfc.report.service;


import com.sfc.report.config.ReportConfig;
import com.sfc.report.mapper.ReportMapper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

@Service
public class generateservice {
    @Autowired
    private ReportConfig reportConfig;
    @Autowired
    private ReportMapper reportMapper;

    /**
     * reportservice
     */
    public void reportservice(String product, String beginDate, String endDate, HttpServletResponse response){
        String sqlpath=reportConfig.getSqlpath();
        //String bDate=" '"+beginDate+"' ";
        //String eDate=" '"+endDate+"' ";
        String excelfilename=reportConfig.getFilename();
        List<String> filenames=null;
        if(sqlpath!=null) {
            filenames = getsqlfilename(sqlpath);
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        for(String filename: filenames){
            String sql=null;
            try{
                sql = readSqlFile(filename);
               // sql = sql.replaceAll(reportConfig.getProduct(), product);
                //sql = sql.replaceAll(reportConfig.getBeginDate(), beginDate);
                //sql = sql.replaceAll(reportConfig.getEndDate(), endDate);
                //String ss=Matcher.quoteReplacement("${PFID}");
                sql = sql.replace("${PFID}", product)
                         .replace("${FBEGINDATE}", beginDate)
                         .replace("${FENDDATE}", endDate );
            }catch (Exception e){
                e.printStackTrace();
            }
            //System.out.println(sql);
            String sheetename=filename.substring(filename.lastIndexOf("\\")+1);
            //sheet名与sql文件名一致
            sheetename=sheetename.substring(0,sheetename.indexOf("."));
            System.out.println(sheetename);
            HSSFSheet sheet = workbook.createSheet(sheetename);
            List<List<Object>> report=null;
            try {
                report = excuteSql(sql);
            }catch(Exception e){
                e.printStackTrace();
                System.out.println(sql);
            }

            if(report!=null&&report.size()>0) {
                saveToExcel(report, sheetename, response, workbook,  sheet);
            }

        }
        try {
            //清空response
            response.reset();
            //设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename= "+excelfilename+".xls");
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream;charset=UTF-8");
            //将excel写入到输出流中
            workbook.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 遍历sql文件
     */
    public List<String> getsqlfilename(String sqlpath){
        File dir = new File(sqlpath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        List<String> filenames=new ArrayList<String>();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if(fileName.endsWith(".sql")){
                    filenames.add(files[i].getAbsolutePath());
                }

            }

        }
        return filenames;
    }

    /**
     * 保存excel文件
     */
    public void saveToExcel(List<List<Object>> result, String filename, HttpServletResponse response, HSSFWorkbook workbook , HSSFSheet sheet){


        for (int i = 0; i < result.size(); i++) {
            // 一个List对象是一个Map，一行数据，一个Map对象对应一行里的一条数据
            List<Object> tableMap = result.get(i);
            //表头是第0行，所以从第一行开始创建
            Row row = sheet.createRow(i);
            //循环创建单元格
            for (int j = 0; j < tableMap.size(); j++) {
                //获取指定字段的值，判断是否为空
                Object object=tableMap.get(j);
                String val="";
                if(object!=null){
                    val=object.toString();
                }
                row.createCell(j).setCellValue(val);
            }
        }

    }


    /**
     * 执行sql 获取二维数组
     */
    public List<List<Object>> excuteSql(String sql) {
        List<List<Object>> result = new ArrayList<>();
        List<Map<String, Object>> tempData = reportMapper.generate(sql);

        if(tempData.size()<=0) {
            try{
            Class.forName(reportConfig.getDriver());
            Connection conn = (Connection) DriverManager.getConnection(reportConfig.getUrl(), reportConfig.getUsername(), reportConfig.getPassword());
            PreparedStatement prs = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet res = prs.executeQuery();
            ResultSetMetaData rsmd = res.getMetaData();
            List<Object> rows=new ArrayList<Object>();
            int nums =rsmd.getColumnCount();
            for(int i=1;i<=nums;i++){
                rows.add(rsmd.getColumnName(i));
            }
            result.add(rows);
            res.close();
            conn.close();
            prs.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Map<String, Object> Header=null;
        if(tempData.size()>0) {
            Header = tempData.get(0);
            List<Object> HeaderList = new ArrayList<>();
        if(Header!=null) {

            for (String keyName : Header.keySet()) {
                HeaderList.add(keyName);
            }
        }
        result.add(HeaderList);
        }
        for (int i = 0; i < tempData.size(); i++) {
            Map<String, Object> tempMap = tempData.get(i);
            List<Object> tempList = new ArrayList<>();
            if(tempMap!=null) {
                for (String keyName : tempMap.keySet()) {
                    tempList.add(tempMap.get(keyName));
                }
            }
            result.add(tempList);
        }

        return result;
    }


    /**
     * 读取SQL文件内容
     */
    public String readSqlFile(String fileName)  {
        File file = new File(fileName);
        StringBuffer sql = new StringBuffer();
        InputStream is=null;
        BufferedReader br=null;
        try{
        is = new FileInputStream(fileName);
         br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String temp;
        while ((temp = br.readLine()) != null) {
            sql.append(temp).append("\n");
        }
            is.close();
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return sql.toString();
    }


    /**
     * 产品参数化
     */
    public String productParam(String product){

        if("is not null".equalsIgnoreCase(product)){
            return " is not null ";
        } else{
            String[] productVarietyArr = product.split(",");
            StringBuilder productParam = new StringBuilder(" ");
            if (productVarietyArr.length > 0) {
                for (int i = 0; i < productVarietyArr.length; i++) {
                    productParam.append("'").append(productVarietyArr[i]).append("',");
                }
            }
            String sql = productParam.substring(0, productParam.lastIndexOf(","));
            sql = " in (" + sql + ") ";
            return sql;
        }
    }



}
