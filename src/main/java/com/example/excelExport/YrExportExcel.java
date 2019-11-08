package com.example.excelExport;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.example.DateUtil;
import com.example.User;
import com.example.UuidUtil;

import lombok.Data;

@Component
@Data
public class YrExportExcel {

    public static void main(String[] args) {
        List<User> dtos = new ArrayList<>();
        User a = new User();
        a.setName("阿斯顿");
        a.setAge("18");
        dtos.add(a);
        User b = new User();
        b.setName("徐正曦");
        b.setAge("18");
        dtos.add(b);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        YrExportExcel exportExcel = new YrExportExcel();
        exportExcel.setWorkbook(xssfWorkbook);
        exportExcel.createSheet("商户对账报表" + "（" + DateUtil.getDate(new Date(), DateUtil.DATE_FORMAT_3 + "）"),
            new String[] {"序号", "姓名", "年龄", },
            creditCheckFileInfoReportDtoToExcelSheetData2(dtos));
        exportExcel.export();
    }

    private  static List<Object[]> creditCheckFileInfoReportDtoToExcelSheetData2(List<User> dtos) {
        List<Object[]> dataList = new ArrayList<>();
        Object[] objs;
        int i = 1;
        for (User man : dtos) {
            objs = new Object[3];
            objs[0] = i;
            objs[1] = man.getName();
            objs[2] = man.getAge();
            dataList.add(objs);
            i++;
        }
        return dataList;
    }

    private XSSFWorkbook workbook;

    public YrExportExcel createSheet(String sheetName, String[] rowName,
        List<Object[]> dataList) {
        // 创建工作簿对象
        XSSFSheet sheet = workbook.createSheet(sheetName);
        // 创建工作表

        // 产生表格标题行

        XSSFRow rowm = sheet.createRow(0);
        XSSFCell cellTitle = rowm.createCell(0);

        // sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面 - 可扩展】
        XSSFCellStyle columnTopStyle = getColumnTopStyle(workbook);
        XSSFCellStyle style = getStyle(workbook);

        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, (rowName.length - 1)));
        cellTitle.setCellStyle(columnTopStyle);
        cellTitle.setCellValue(sheetName);

        // 定义所需列数
        int columnNum = rowName.length;
        // 在索引2的位置创建行(最顶端的行开始的第二行)
        XSSFRow rowRowName = sheet.createRow(2);

        // 将列头设置到sheet的单元格中
        for (int n = 0; n < columnNum; n++) {
            // 创建列头对应个数的单元格
            XSSFCell cellRowName = rowRowName.createCell(n);
            // 设置列头单元格的数据类型
            cellRowName.setCellType(CellType.STRING);
            XSSFRichTextString text = new XSSFRichTextString(rowName[n]);
            // 设置列头单元格的值
            cellRowName.setCellValue(text);
            // 设置列头单元格样式
            cellRowName.setCellStyle(columnTopStyle);
        }

        // 将查询出的数据设置到sheet对应的单元格中
        for (int i = 0; i < dataList.size(); i++) {

            Object[] obj = dataList.get(i);
            // 创建所需的行数
            XSSFRow row = sheet.createRow(i + 3);

            for (int j = 0; j < obj.length; j++) {
                // 设置单元格的数据类型
                XSSFCell cell = null;
                cell = row.createCell(j, CellType.STRING);
                if (!"".equals(obj[j]) && obj[j] != null) {
                    // 设置单元格的值
                    cell.setCellValue(obj[j].toString());
                }
                // 设置单元格样式
                cell.setCellStyle(style);
            }
        }
        // 让列宽随着导出的列长自动适应
        for (int colNum = 0; colNum < columnNum; colNum++) {
            int columnWidth = sheet.getColumnWidth(colNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                XSSFRow currentRow;
                // 当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }
                if (currentRow.getCell(colNum) != null) {
                    XSSFCell currentCell = currentRow.getCell(colNum);
                    if (currentCell.getCellType() == CellType.STRING) {
                        int length = currentCell.getStringCellValue()
                            .getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            if (colNum == 0) {
                sheet.setColumnWidth(colNum, (columnWidth - 2) * 256);
            } else {
                sheet.setColumnWidth(colNum, (columnWidth + 4) * 256);
            }
        }
        return this;
    }

    public String export() {
        //输出Excel文件
        OutputStream output;
        try {
            if (workbook != null) {
                String fileName = "报表统计" + UuidUtil.nextId() + ".xlsx";
                // 上传到server
                // ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // workbook.write(bos);
                // return fileProcessServiceFactory.getInstance()
                //     .uploadFile(fileName, bos.toByteArray());
                
                // main导出
                output = new FileOutputStream("F:/" + fileName + ".xls");

                workbook.write(output);
                output.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String export(XSSFWorkbook workbook, String fileName) {
        OutputStream output;
        try {
            if (workbook != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                workbook.write(bos);
                output = new FileOutputStream("F:/" + fileName + ".xls");

                workbook.write(output);
                output.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private XSSFCellStyle getColumnTopStyle(XSSFWorkbook workbook) {
        XSSFCellStyle xssfCellStyle = styleConfig(workbook);
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setBold(false);
        font.setFontName("Courier New");
        xssfCellStyle.setBorderBottom(BorderStyle.THIN);
        xssfCellStyle.setBottomBorderColor(new XSSFColor(Color.BLACK));
        return xssfCellStyle;

    }

    /**
     * 列数据信息单元格样式
     *
     * @param workbook
     * @return
     */
    private XSSFCellStyle getStyle(XSSFWorkbook workbook) {
        XSSFCellStyle xssfCellStyle = styleConfig(workbook);
        XSSFFont font = workbook.createFont();
        font.setFontName("Courier New");
        xssfCellStyle.setFont(font);
        return xssfCellStyle;
    }

    private XSSFCellStyle styleConfig(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(new XSSFColor(Color.BLACK,new DefaultIndexedColorMap()));
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(new XSSFColor(Color.BLACK,new DefaultIndexedColorMap()));
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(new XSSFColor(Color.BLACK,new DefaultIndexedColorMap()));
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(new XSSFColor(Color.BLACK,new DefaultIndexedColorMap()));
        style.setWrapText(false);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
