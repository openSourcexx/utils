package com.example.excelExport;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.DateUtil;
import com.example.annotation.Excel;
import com.example.constant.FileSuffixConstant;
import com.example.enums.EnumExportType;
import com.example.exception.BizException;

import javax.servlet.http.HttpServletResponse;

/**
 * excel导出工具类
 * 2018/12/5 15:51
 *
 * @author admin
 */
public class ExcelUtil {
    private static Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    private static String path = "E:\\";

    /**
     * 通过注解单行导出
     *
     * @param dataList
     * @param clazz
     * @param fileName
     * @param exportType
     * @return
     */
    public static boolean exportByAnnotation(List<?> dataList, Class<?> clazz, String fileName,
        EnumExportType exportType) {
        //创建HSSFWorkbook对象(excel的文档对象)
        XSSFWorkbook wb = new XSSFWorkbook();
        //建立新的sheet对象（excel的表单）
        XSSFSheet sheet = wb.createSheet(fileName);

        // 1.生成字体对象
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("新宋体");

        // 2.设置样式
        XSSFCellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setWrapText(true);
        // 居中
        style.setAlignment(HorizontalAlignment.CENTER);

        createHeaderRow(sheet, style, clazz);
        createBodyRow(sheet, style, dataList);

        buildOutput(wb, fileName, exportType);
        return true;
    }

    private static void buildOutput(XSSFWorkbook wb, String fileName, EnumExportType type) {
        //输出Excel文件
        OutputStream output;
        try {
            switch (type) {
                case UPLOAD_SERVER:
                    break;
                case STREAM:
                    // output = response.getOutputStream();
                    // response.reset();
                    //
                    // response.setContentType("application/octet-stream;charset=utf-8");
                    // response.setHeader("Content-Disposition",
                    //     "attachment;filename=" + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1) + ".xlsx");
                    // wb.write(output);
                    // output.close();
                    break;
                default:
                    output = new FileOutputStream(path + fileName + FileSuffixConstant.XLSX);
                    wb.write(output);
                    output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成标题行
     *
     * @param sheet
     * @param style
     * @param clazz
     */
    private static void createHeaderRow(XSSFSheet sheet, XSSFCellStyle style, Class<?> clazz) {
        //在sheet里创建第一行
        XSSFRow row1 = sheet.createRow(0);
        Field[] fields = clazz.getDeclaredFields();
        int num = 0;
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Excel annotation = field.getDeclaredAnnotation(Excel.class);
            XSSFCell hSSFCell = row1.createCell(num);
            hSSFCell.setCellStyle(style);
            if (annotation != null) {
                hSSFCell.setCellValue(annotation.title());
            }
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
    private static void createBodyRow(XSSFSheet sheet, XSSFCellStyle style, List<?> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            //从sheet第二行开始填充数据
            XSSFRow rowN = sheet.createRow(i + 1);
            Object obj = dataList.get(i);
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            int j = 0;
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                Excel annotation = field.getDeclaredAnnotation(Excel.class);
                if (annotation == null) {
                    continue;
                }
                Object value = genBodyValueByField(clazz, obj, field);
                XSSFCell cell00 = rowN.createCell(j);
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
        PropertyDescriptor pd;
        String type = null;
        Object value;
        try {
            pd = new PropertyDescriptor(field.getName(), clazz);
            Method m = pd.getReadMethod();
            type = field.getGenericType()
                .toString();
            value = m.invoke(obj);
        } catch (Exception e) {
            log.error("类:{},{}属性方法执行异常,属性类型:{}", clazz, field.getName(), type);
            return null;
        }
        switch (type) {
            case "String":
            case "class java.lang.String":
                return value;
            case "int":
            case "class java.lang.Integer":
                return (int) value;
            case "double":
            case "class java.lang.Double":
                return (double) value;
            case "long":
            case "class java.lang.Long":
                return (long) value;
            case "float":
            case "class java.lang.Float":
                return (float) value;
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
     *
     * @param response
     * @param list list内为每行数据，LinkedHashMap中按次序存放某一行中每一列的数据，key为每列字段中文名，value为具体数据
     * @param fileName 文件名
     * @param model 标题行数据和起始列集合
     * @param merge 每行合并列集合
     * @return
     */
    public static boolean exportExt(HttpServletResponse response, ArrayList<LinkedHashMap<String, String>> list,
        String fileName, ExcelExportExtTitleModel model, List<CellRangeAddress> merge) {
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
        // 调用字体样式对象
        style.setFont(font);
        style.setWrapText(true);
        // 设置居中样式
        style.setAlignment(HorizontalAlignment.CENTER);

        // 动态合并列
        merge.forEach(sheet::addMergedRegion);

        // 创建第一行
        HSSFRow row0 = sheet.createRow(0);
        int num = 0;
        List<String> head0 = model.getTitle()
            .get("head0");
        for (String field : head0) {
            HSSFCell hSSFCell = row0.createCell(num);
            hSSFCell.setCellStyle(style);
            hSSFCell.setCellValue(field);
            //单元格宽度自适应
            sheet.autoSizeColumn((short) num, true);
            num++;
        }

        // 动态补充第二行数据
        HSSFRow row1 = sheet.createRow(1);
        List<String> cols = model.getCols();
        if (cols.size() > 0) {
            for (int i = 0; i < cols.size(); i++) {
                int n = Integer.valueOf(cols.get(i));
                List<String> headn = model.getTitle()
                    .get("head" + (i + 1));
                for (String field : headn) {
                    HSSFCell hSSFCell = row1.createCell(n);
                    hSSFCell.setCellStyle(style);
                    hSSFCell.setCellValue(field);
                    //单元格宽度自适应
                    sheet.autoSizeColumn((short) n, true);
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
                sheet.autoSizeColumn((short) j, true);
                j++;
            }

        }

        //输出Excel文件
        OutputStream output;
        try {
            // web导出
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
     * @param startRowNum 开始读取的行数，最小为1
     * @return 文件内容
     */
    public static List<List<String>> readExcel(InputStream fileStream, int startRowNum) {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileStream);
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            return readExcel(xssfWorkbook, startRowNum, xssfSheet.getLastRowNum() - startRowNum + 1);
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
     * 获取表格中的值
     *
     * @param cell 表格
     * @return 值
     */
    private static String getStringVal(XSSFCell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    public static void main(String[] args) throws FileNotFoundException {

        List<PerformanceDto> dtoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PerformanceDto pojo = new PerformanceDto();
            pojo.setUsername(i + "a");
            pojo.setHandleCount((long) i);
            pojo.setApprovalRate(new BigDecimal(i + 2));
            pojo.setDate(new Date());
            pojo.setCount(i);
            pojo.setDemo("111");
            dtoList.add(pojo);
        }
        // exportByAnnotation(dtoList, PerformanceDto.class, ExcelReportConstants.STAFF_PERFORMANCE, EnumExportType.LOCAL);
        InputStream in = new FileInputStream(new File("E:\\员工绩效统计.xlsx"));
        List<List<String>> list = readExcel(in);
        System.out.println();
    }
}
