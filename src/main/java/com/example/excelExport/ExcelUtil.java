package com.example.excelExport;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.DateUtil;
import com.example.UuidUtil;
import com.example.annotation.Excel;
import com.example.exception.BizException;

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


    /**
     * 从第一行开始读取excel文件
     *
     * @param fileStream 文件流
     * @return 文件内容
     */
    public static List<List<String>> readExcel(InputStream fileStream) {
        return readExcel(fileStream, 1);
    }

    /**
     * 从第rowNum开始读取excel文件
     *
     * @param fileStream 文件流
     * @param rowNum 开始读取的行数，最小为1
     * @return 文件内容
     */
    public static List<List<String>> readExcel(InputStream fileStream, int rowNum) {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileStream);
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            return readExcel(xssfWorkbook, rowNum, xssfSheet.getLastRowNum() - rowNum + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从rowNum开始读取数据，读size行
     *
     * @param fileStream 文件流
     * @param rowNum 开始读取的行数，最小为1
     * @param size 读取多少行
     * @return 文件内容
     */
    public static List<List<String>> readExcel(InputStream fileStream, int rowNum, int size) {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileStream);
            return readExcel(xssfWorkbook, rowNum, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从rowNum开始读取数据，读size行
     *
     * @param workbook excel文件
     * @param rowNum 开始读取的行数，最小为1
     * @param size 读取多少行
     * @return 文件内容
     */
    public static List<List<String>> readExcel(XSSFWorkbook workbook, int rowNum, int size) {
        try {
            List<List<String>> result = Lists.newArrayList();
            XSSFSheet xssfSheet = workbook.getSheetAt(0);
            if (xssfSheet == null) {
                throw new BizException("excel文件为空");
            }
            int count = size + rowNum - 1;
            for (; rowNum <= count; rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow == null) {
                    continue;
                }
                int minColIx = xssfRow.getFirstCellNum();
                int maxColIx = xssfRow.getLastCellNum();
                List<String> rowList = Lists.newArrayList();
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    XSSFCell cell = xssfRow.getCell(colIx);
                    if (cell == null) {
                        continue;
                    }
                    rowList.add(getStringVal(cell));
                }

                // 如果前三列都为空，则认为解析完毕
                if (CollectionUtils.isEmpty(rowList)) {
                    break;
                }

                result.add(rowList);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据data生成excel并在本地生成文件
     *
     * @param data 要设置的数据
     * @param filePathWithName 生成文件的路径，带文件名
     * @return 生成的文件File
     */
    public static File create(List<List<String>> data, String filePathWithName) {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
            XSSFSheet xssfSheet = xssfWorkbook.createSheet();
            // 遍历行
            generateCellValue(data, xssfSheet);

            File nFile = new File(filePathWithName);
            OutputStream os = new FileOutputStream(nFile);
            xssfWorkbook.write(os);
            os.flush();
            os.close();
            return nFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取表格中的值
     *
     * @param cell 表格
     * @return 值
     */
    private static String getStringVal(XSSFCell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    public static String renameFile(String fileName) {
        return UuidUtil.nextId() + fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 创建sheet
     */
    public static void createSheet(List<List<String>> data, int sheetIndex, XSSFWorkbook xssfWorkbook) {
        XSSFSheet xssfSheet = xssfWorkbook.createSheet();
        xssfWorkbook.setSheetName(sheetIndex - 1, "第" + sheetIndex + "页");

        generateCellValue(data, xssfSheet);
    }

    /**
     * 把data中的参数设置到excel中
     *
     * @param data 参数
     * @param xssfSheet 表格
     */
    private static void generateCellValue(List<List<String>> data, XSSFSheet xssfSheet) {
        // 遍历行
        for (int i = 0; i < data.size(); i++) {
            XSSFRow row = xssfSheet.createRow(i);
            // 遍历列
            List<String> rowData = data.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(rowData.get(j));
            }

        }
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
        export(null,dtoList,PerformanceDto.class,ReportConstants.STAFF_PERFORMANCE);

    }
}
