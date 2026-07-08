package com.admin.sys.base.module.quartz.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.admin.sys.base.module.extend.entity.BaseEntity;
import com.admin.sys.base.annotation.ExcelField;

import java.util.Date;

/**
*【任务信息】实体对象
*/

@TableName("sys_quartz")
public class SysQuartz extends BaseEntity {
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";
    /*** 主键id */
    private String id;
    /*** 名称 */
    private String jobName;
    /*** 目标对象 */
    private String beanName;
    /*** 参数 */
    private String params;
    /*** 表达式 */
    private String cronExpression;
    /*** 类型 */
    private Integer jobType;
    /*** 是否有参数 */
    private Integer hasParams;
    /*** 目标方法 */
    private String methodName;
    /*** 执行状态 */
    private Integer jobStatus;
    /*** 是否启动：0暂停 1启动 */
    private Integer isExec;
    /*** 最后执行时间 */
    private Date lastExecTime;
    /*** 备注信息 */
    private String remarks;
    @ExcelField(title="主键id",align=1, sort=0)
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    @ExcelField(title="名称",align=1, sort=1)
    public String getJobName() {
        return this.jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    @ExcelField(title="目标对象",align=1, sort=2)
    public String getBeanName() {
        return this.beanName;
    }
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
    @ExcelField(title="参数",align=1, sort=3)
    public String getParams() {
        return this.params;
    }
    public void setParams(String params) {
        this.params = params;
    }
    @ExcelField(title="表达式",align=1, sort=4)
    public String getCronExpression() {
        return this.cronExpression;
    }
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
    @ExcelField(title="类型",dictType="job_type",align=1, sort=5)
    public Integer getJobType() {
        return this.jobType;
    }
    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }
    @ExcelField(title="是否有参数",dictType="sys_yes_no",align=1, sort=6)
    public Integer getHasParams() {
        return this.hasParams;
    }
    public void setHasParams(Integer hasParams) {
        this.hasParams = hasParams;
    }
    @ExcelField(title="目标方法",align=1, sort=7)
    public String getMethodName() {
        return this.methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    @ExcelField(title="状态",dictType="job_status",align=1, sort=8)
    public Integer getJobStatus() {
        return this.jobStatus;
    }
    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }
    @ExcelField(title="是否执行",dictType="sys_yes_no",align=1, sort=9)
    public Integer getIsExec() {
        return this.isExec;
    }
    public void setIsExec(Integer isExec) {
        this.isExec = isExec;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ExcelField(title="最后执行时间",align=1, sort=10)
    public Date getLastExecTime() {
        return this.lastExecTime;
    }
    public void setLastExecTime(Date lastExecTime) {
        this.lastExecTime = lastExecTime;
    }
    @ExcelField(title="备注信息",align=1, sort=11)
    public String getRemarks() {
        return this.remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}