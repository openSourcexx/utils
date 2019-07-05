package com.example.excelExport;

import java.io.Serializable;
import java.math.BigDecimal;

import com.example.annotation.Excel;

/**
 * 员工绩效
 */
public class PerformanceDto implements Serializable{
    /** 员工姓名 */
    @Excel(name = "员工姓名")
    private String username;

    /** 人工进件量 */
    @Excel(name = "人工进件量")
    private long handleCount = 0L;

    /** 通过率 */
    @Excel(name = "通过率")
    private BigDecimal approvalRate;

    /** 电核时效 */
    @Excel(name = "电核时效")
    private BigDecimal telAging;

    /** 非电核时效 */
    @Excel(name = "电核时效")
    private BigDecimal notTelAging;

    /** 审批时效 */
    @Excel(name = "审批时效")
    private BigDecimal approvalAging;

    /** 挂起时效 */
    @Excel(name = "挂起时效")
    private BigDecimal holdAging;

    /** 总时效 */
    @Excel(name = "总时效")
    private BigDecimal totalAging;

    /** 挂起数量 */
    @Excel(name = "挂起数量")
    private long holdCount;

    /** 超时案件数量 */
    @Excel(name = "超时案件数量")
    private long overTimeCount;

    /** 通过改拒绝 */
    @Excel(name = "通过改拒绝")
    private BigDecimal finalRefuseRate;

    /** 通过改额度 */
    @Excel(name = "通过改额度")
    private BigDecimal amtChangeRate;

    /** 反馈反欺诈案件笔数 */
    @Excel(name = "反馈反欺诈案件笔数")
    private long antiFraudCount;

    /** 驳回率*/
    @Excel(name = "驳回率")
    private BigDecimal rejectRate;

    /** 主动复核案件笔数*/
    @Excel(name = "主动复核案件笔数")
    private long reviewCount;

    public PerformanceDto() {
        this.approvalRate = BigDecimal.ZERO;
        this.telAging = BigDecimal.ZERO;
        this.notTelAging = BigDecimal.ZERO;
        this.approvalAging = BigDecimal.ZERO;
        this.holdAging = BigDecimal.ZERO;
        this.totalAging = BigDecimal.ZERO;
        this.holdCount = 0L;
        this.overTimeCount = 0L;
        this.finalRefuseRate = BigDecimal.ZERO;
        this.amtChangeRate = BigDecimal.ZERO;
        this.antiFraudCount = 0L;
        this.rejectRate = BigDecimal.ZERO;
        this.reviewCount = 0L;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getHandleCount() {
        return handleCount;
    }

    public void setHandleCount(long handleCount) {
        this.handleCount = handleCount;
    }

    public BigDecimal getApprovalRate() {
        return approvalRate;
    }

    public void setApprovalRate(BigDecimal approvalRate) {
        this.approvalRate = approvalRate;
    }

    public BigDecimal getTelAging() {
        return telAging;
    }

    public void setTelAging(BigDecimal telAging) {
        this.telAging = telAging;
    }

    public BigDecimal getNotTelAging() {
        return notTelAging;
    }

    public void setNotTelAging(BigDecimal notTelAging) {
        this.notTelAging = notTelAging;
    }

    public BigDecimal getApprovalAging() {
        return approvalAging;
    }

    public void setApprovalAging(BigDecimal approvalAging) {
        this.approvalAging = approvalAging;
    }

    public BigDecimal getHoldAging() {
        return holdAging;
    }

    public void setHoldAging(BigDecimal holdAging) {
        this.holdAging = holdAging;
    }

    public BigDecimal getTotalAging() {
        return totalAging;
    }

    public void setTotalAging(BigDecimal totalAging) {
        this.totalAging = totalAging;
    }

    public long getHoldCount() {
        return holdCount;
    }

    public void setHoldCount(long holdCount) {
        this.holdCount = holdCount;
    }

    public long getOverTimeCount() {
        return overTimeCount;
    }

    public void setOverTimeCount(long overTimeCount) {
        this.overTimeCount = overTimeCount;
    }

    public BigDecimal getFinalRefuseRate() {
        return finalRefuseRate;
    }

    public void setFinalRefuseRate(BigDecimal finalRefuseRate) {
        this.finalRefuseRate = finalRefuseRate;
    }

    public BigDecimal getAmtChangeRate() {
        return amtChangeRate;
    }

    public void setAmtChangeRate(BigDecimal amtChangeRate) {
        this.amtChangeRate = amtChangeRate;
    }

    public long getAntiFraudCount() {
        return antiFraudCount;
    }

    public void setAntiFraudCount(long antiFraudCount) {
        this.antiFraudCount = antiFraudCount;
    }

    public BigDecimal getRejectRate() {
        return rejectRate;
    }

    public void setRejectRate(BigDecimal rejectRate) {
        this.rejectRate = rejectRate;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(long reviewCount) {
        this.reviewCount = reviewCount;
    }
}