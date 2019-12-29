package com.example.excelExport;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.example.annotation.Excel;

import lombok.Data;

/**
 * 员工绩效
 */
@Data
public class PerformanceDto implements Serializable{
    private static final long serialVersionUID = 2908822730085475038L;
    /** 员工姓名 */
    @Excel(title = "员工姓名")
    private String username;

    /** 人工进件量 */
    @Excel(title = "人工进件量")
    private long handleCount = 0L;

    /** 通过率 */
    @Excel(title = "通过率")
    private BigDecimal approvalRate;

    @Excel(title = "日期")
    private Date date;

    @Excel(title = "总数")
    private int count;
    @Excel(title = "测试")
    private String demo;
}