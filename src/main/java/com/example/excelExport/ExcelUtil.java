package com.example.excelExport;

import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.DateUtil;
import com.example.annotation.Excel;

import javax.servlet.http.HttpServletResponse;

/**
 * excel导出工具类
 * 2018/12/5 15:51
 */
public class ExcelUtil {
    private static Logger log = LoggerFactory.getLogger(ExcelUtil.class);
    /**
     * 普通单行导出
     *
     * @param dataList
     * @param fileName 文件名
     * @param response
     * @return
     */
    public static boolean export(HttpServletResponse response, List<?> dataList, Class<?> clazz, String fileName) {

        //创建HSSFWorkbook对象(excel的文档对象)
        HSSFWorkbook wb = new HSSFWorkbook();
        //建立新的sheet对象（excel的表单）
        HSSFSheet sheet = wb.createSheet(fileName);

        // 1.生成字体对象
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("新宋体");

        // 2.设置样式
        HSSFCellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setWrapText(true);

        createHeaderRow(sheet, style, clazz);
        createBodyRow(sheet, style, dataList);

        //输出Excel文件
        OutputStream output;
        try {
            // output = response.getOutputStream();
            // response.reset();
            //
            // response.setContentType("application/octet-stream;charset=utf-8");
            // response.setHeader("Content-Disposition",
            //     "attachment;filename=" + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1) + ".xls");
            // wb.write(output);
            // output.close();

            // main导出
            output = new FileOutputStream("F:/" + fileName + ".xls");
            wb.write(output);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 生成标题行
     *
     * @param sheet
     * @param style
     * @param clazz
     */
    private static void createHeaderRow(HSSFSheet sheet, HSSFCellStyle style, Class<?> clazz) {
        //在sheet里创建第一行
        HSSFRow row1 = sheet.createRow(0);
        Field[] fields = clazz.getDeclaredFields();
        int num = 0;
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Excel annotation = field.getDeclaredAnnotation(Excel.class);
            HSSFCell hSSFCell = row1.createCell(num);
            hSSFCell.setCellStyle(style);
            hSSFCell.setCellValue(annotation == null ? "" : annotation.name());
            //单元格宽度自适应
            sheet.autoSizeColumn((short) num, true);
            num++;
        }
    }

    /**
     * 生成数据行
     *
     * @param sheet
     * @param style
     * @param dataList
     */
    private static void createBodyRow(HSSFSheet sheet, HSSFCellStyle style, List<?> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            //从sheet第二行开始填充数据
            HSSFRow rowN = sheet.createRow(i + 1);
            Object obj = dataList.get(i);
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            int j = 0;
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                Object value = genBodyValueByField(clazz, obj, field);
                HSSFCell cell00 = rowN.createCell(j);
                cell00.setCellStyle(style);
                cell00.setCellValue(value == null ? "" : String.valueOf(value));
                //单元格宽度自适应
                sheet.autoSizeColumn((short) j, true);
                j++;
            }
        }
    }

    /**
     * 执行getXXX()方法
     *
     * @param clazz
     * @param obj
     * @param field
     * @return
     */
    private static Object genBodyValueByField(Class<?> clazz, Object obj, Field field) {
        PropertyDescriptor pd = null;
        String type = null;
        Object value = null;
        try {
            pd = new PropertyDescriptor(field.getName(), clazz);
            Method m = pd.getReadMethod();
            type = field.getGenericType()
                .toString();
            value = m.invoke(obj);
        } catch (Exception e) {
            log.error("类:{},{}属性方法执行异常,属性类型:{}", clazz, field.getName(),type);
            return null;
        }
        switch (type) {
            case "String":
            case "class java.lang.String":
                return (String) value;
            case "int":
            case "class java.lang.Integer":
                return (Integer) value;
            case "double":
            case "class java.lang.Double":
                return (Double) value;
            case "float":
            case "class java.lang.Float":
                return (Float) value;
            case "char":
            case "class java.lang.Character":
                return (Character) value;
            case "class java.math.BigDecimal":
                return (BigDecimal) value;
            case "class java.util.Date":
                return (String) DateUtil.getDate((Date) value, DateUtil.DATE_FORMAT_2);
            default:
                return "";
        }
    }


    /**
     * 动态合并列导出
     * @param response
     * @param list list内为每行数据，LinkedHashMap中按次序存放某一行中每一列的数据，key为每列字段中文名，value为具体数据
     * @param fileName 文件名
     * @param model 标题行数据和起始列集合
     * @param merge 每行合并列集合
     * @return
     */
    public static boolean exportExt(HttpServletResponse response, ArrayList<LinkedHashMap<String,String>> list, String fileName,ExcelExportExtTitleModel model,List<CellRangeAddress> merge) {
        //创建HSSFWorkbook对象(excel的文档对象)
        HSSFWorkbook wb = new HSSFWorkbook();
        //建立新的sheet对象（excel的表单）
        HSSFSheet sheet = wb.createSheet(fileName);

        // 1.生成字体对象
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("新宋体");

        // 2.生成样式对象，这里的设置居中样式和版本有关，我用的poi用HSSFCellStyle.ALIGN_CENTER会报错，所以用下面的
        HSSFCellStyle style = wb.createCellStyle();
        style.setFont(font); // 调用字体样式对象
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);//设置居中样式

        //动态合并列
        merge.forEach(sheet::addMergedRegion);

        //创建第一行
        HSSFRow row0 = sheet.createRow(0);
        int num = 0;
        List<String> head0 = model.getTitle().get("head0");
        for (String field : head0) {
            HSSFCell hSSFCell = row0.createCell(num);
            hSSFCell.setCellStyle(style);
            hSSFCell.setCellValue(field);
            //单元格宽度自适应
            sheet.autoSizeColumn((short) num,true);
            num++;
        }

        // 动态补充第二行数据
        HSSFRow row1 = sheet.createRow(1);
        List<String> cols = model.getCols();
        if (cols.size() > 0) {
            for (int i = 0; i < cols.size(); i++) {
                int n = Integer.valueOf(cols.get(i));
                List<String> headn = model.getTitle().get("head" + (i + 1));
                for (String field : headn) {
                    HSSFCell hSSFCell = row1.createCell(n);
                    hSSFCell.setCellStyle(style);
                    hSSFCell.setCellValue(field);
                    //单元格宽度自适应
                    sheet.autoSizeColumn((short) n,true);
                    n++;
                }
            }
        }

        for (int i = 0; i < list.size(); i++) {

            //从sheet第三行开始填充数据
            HSSFRow rowx = sheet.createRow(i + 2);
            LinkedHashMap<String, String> linkMap = list.get(i);

            int j = 0;
            for (Map.Entry<String, String> entry : linkMap.entrySet()) {
                HSSFCell cell00 = rowx.createCell(j);
                cell00.setCellStyle(style);
                cell00.setCellValue(entry.getValue());
                //单元格宽度自适应
                sheet.autoSizeColumn((short) j,true);
                j++;
            }

        }

        //输出Excel文件
        OutputStream output;
        try {
            // web导出
            /*output = response.getOutputStream();
            response.reset();

            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String(fileName.getBytes(), "iso-8859-1") + ".xls");*/

            // main导出
            output = new FileOutputStream("F:/" + fileName + ".xls");

            wb.write(output);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {

        List<PerformanceDto> dtoList = new ArrayList<>();
        for (int i=0;i<10;i++) {
            PerformanceDto pojo = new PerformanceDto();
            pojo.setUsername(i+"a");
            pojo.setHandleCount(i);
            pojo.setApprovalRate(new BigDecimal(i+2));
            pojo.setTelAging(new BigDecimal(i+3));
            pojo.setNotTelAging(new BigDecimal(i+3));
            pojo.setApprovalAging(new BigDecimal(i+3));
            pojo.setHoldAging(new BigDecimal(i+4));
            pojo.setTotalAging(new BigDecimal(i+4));
            pojo.setHoldCount(i+6);
            pojo.setOverTimeCount(i+6);
            pojo.setFinalRefuseRate(new BigDecimal(i+7));
            pojo.setAmtChangeRate(new BigDecimal(i+8));
            pojo.setAntiFraudCount(i+4);
            pojo.setRejectRate(new BigDecimal(i+9));
            pojo.setReviewCount(i+3);
            dtoList.add(pojo);
        }
        //
        //
        // ArrayList<LinkedHashMap<String, String>> respList = new ArrayList<>();
        // //按展示要求组装
        // dtoList.forEach(pojo -> {
        //     LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //     DecimalFormat df = new DecimalFormat("0%");
        //     map.put(ReportConstants.USER_NAME, pojo.getUsername());
        //     map.put(ReportConstants.HANDLE_COUNT, String.valueOf(pojo.getHandleCount()));
        //     map.put(ReportConstants.PASS_RATE, df.format(pojo.getApprovalRate()));
        //     map.put(ReportConstants.TEL_AGING, String.valueOf(pojo.getTelAging()));
        //     map.put(ReportConstants.NOT_TEL_AGING, String.valueOf(pojo.getNotTelAging()));
        //     map.put(ReportConstants.APPROVAL_AGING, String.valueOf(pojo.getApprovalAging()));
        //     map.put(ReportConstants.HOLD_AGING, String.valueOf(pojo.getHoldAging()));
        //     map.put(ReportConstants.TOTAL_AGING, String.valueOf(pojo.getTotalAging()));
        //     map.put(ReportConstants.HOLD_COUNT, String.valueOf(pojo.getHoldCount()));
        //     map.put(ReportConstants.OVER_TIME_COUNT, String.valueOf(pojo.getOverTimeCount()));
        //     map.put(ReportConstants.FINAL_REFUSE_RATE, df.format(pojo.getFinalRefuseRate()));
        //     map.put(ReportConstants.AMT_CHANGE_RATE, df.format(pojo.getAmtChangeRate()));
        //     map.put(ReportConstants.ANTI_FRAUD_COUNT, String.valueOf(pojo.getAntiFraudCount()));
        //     map.put(ReportConstants.REJECT_RATE, df.format(pojo.getRejectRate()));
        //     map.put(ReportConstants.REVIEW_COUNT, String.valueOf(pojo.getReviewCount()));
        //     respList.add(map);
        // });
        // Map<String,List<String>> title = new HashMap<>();
        // List<String> head0 = new ArrayList<>();
        // head0.add(ReportConstants.USER_NAME);
        // head0.add(ReportConstants.HANDLE_COUNT);
        // head0.add(ReportConstants.MAN_APPROVAL_PASS_RATE);
        //
        // head0.add(ReportConstants.MAN_APPROVAL_AVERAGE_AGING);
        // head0.add(ReportConstants.SPACE);
        // head0.add(ReportConstants.SPACE);
        // head0.add(ReportConstants.SPACE);
        // head0.add(ReportConstants.SPACE);
        //
        // head0.add(ReportConstants.HOLD_COUNT);
        // head0.add(ReportConstants.OVER_TIME_COUNT);
        //
        // head0.add(ReportConstants.TOTAL_MAN_CHANGE);
        // head0.add(ReportConstants.SPACE);
        //
        // head0.add(ReportConstants.ANTI_FRAUD_COUNT);
        // head0.add(ReportConstants.REJECT_RATE);
        // head0.add(ReportConstants.REVIEW_COUNT);
        // // 标题第一行
        // title.put("head0",head0);
        // List<String> head1 = new ArrayList<>();
        //
        // head1.add(ReportConstants.TEL_AGING);
        // head1.add(ReportConstants.NOT_TEL_AGING);
        // head1.add(ReportConstants.APPROVAL_AGING);
        // head1.add(ReportConstants.HOLD_AGING);
        // head1.add(ReportConstants.TOTAL_AGING);
        //
        // // 标题第二行
        // title.put("head1",head1);
        //
        // List<String> head2 = new ArrayList<>();
        // head2.add(ReportConstants.FINAL_REFUSE_RATE);
        // head2.add(ReportConstants.AMT_CHANGE_RATE);
        // title.put("head2",head2);
        //
        // List<String> cols = new ArrayList<>();
        //
        // // 合并开始的列
        // int firstCol0 = 3;
        // int lastCol0 = 7;
        // int firstCol1 = 10;
        // int lastCol1 = 11;
        // cols.add(String.valueOf(firstCol0));
        // cols.add(String.valueOf(firstCol1));
        //
        // List<CellRangeAddress> merge = new ArrayList<>();
        // CellRangeAddress region = new CellRangeAddress(0,0,firstCol0,lastCol0);
        // CellRangeAddress region2 = new CellRangeAddress(0,0,firstCol1,lastCol1);
        // merge.add(region);
        // merge.add(region2);
        // for (int i = 0; i < respList.get(0).size();i++) {
        //     if (i >= firstCol0 && i <= lastCol0) {
        //         continue;
        //     } else if (i >= firstCol1 && i <= lastCol1) {
        //         continue;
        //     }
        //     region = new CellRangeAddress(0,1,i,i);
        //     merge.add(region);
        // }
        // ExcelExportExtTitleModel model = new ExcelExportExtTitleModel();
        // model.setTitle(title);
        // model.setCols(cols);
        // //导出
        // ExcelUtil.exportExt(null, respList, ReportConstants.STAFF_PERFORMANCE, model,merge);
        export(null,dtoList,PerformanceDto.class,ReportConstants.STAFF_PERFORMANCE);

    }
}
