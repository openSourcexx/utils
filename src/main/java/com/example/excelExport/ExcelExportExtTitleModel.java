package com.example.excelExport;

import java.util.List;
import java.util.Map;

/**
 * 导出excel合并标题信息
 */
public class ExcelExportExtTitleModel {
    /** 标题行数据 */
    private Map<String,List<String>> title;

    /** 起始合并下标 */
    private List<String> cols;

    public Map<String, List<String>> getTitle() {
        return title;
    }

    public void setTitle(Map<String, List<String>> title) {
        this.title = title;
    }

    public List<String> getCols() {
        return cols;
    }

    public void setCols(List<String> cols) {
        this.cols = cols;
    }
}
