package com.admin.entity;

import java.util.Date;
import java.util.List;
import com.admin.sys.base.annotation.ExcelField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.admin.sys.base.module.extend.entity.BaseEntity;

/**
*【积分信息】实体对象
*/

@TableName("integral")
public class Integral extends BaseEntity {
    /*** 主键id */
    private String id;
    /*** 积分名称 */
    private String name;
    /*** 用户姓名 */
    private String registerId;
    /*** 积分时间 */
    private Date integralTime;
    /*** 积分时间 范围查询 */
    @TableField(exist = false)
    private List<String> integralTimeRange;
    /*** 积分分数 */
    private Integer integralNum;
    /*** 积分说明 */
    private String remarks;
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRegisterId() {
        return this.registerId;
    }
    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    public Date getIntegralTime() {
        return this.integralTime;
    }
    public void setIntegralTime(Date integralTime) {
        this.integralTime = integralTime;
    }
    public List<String> getIntegralTimeRange(){
        return this.integralTimeRange;
    }
    public void setIntegralTimeRange(List<String> integralTimeRange){
        this.integralTimeRange = integralTimeRange;
    }
    public Integer getIntegralNum() {
        return this.integralNum;
    }
    public void setIntegralNum(Integer integralNum) {
        this.integralNum = integralNum;
    }
    public String getRemarks() {
        return this.remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}