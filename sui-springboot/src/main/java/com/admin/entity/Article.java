package com.admin.entity;

import com.admin.sys.base.module.extend.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;

/**
*【文章信息】实体对象
*/

@TableName("article")
public class Article extends BaseEntity {
    /*** 主键id */
    private String id;
    /*** 文章图片 */
    private String picture;
    /*** 文章标题 */
    private String title;
    /*** 文章作者 */
    private String userId;
    /*** 发布时间 */
    private Date publishTime;
    /*** 发布时间 范围查询*/
    @TableField(exist = false)
    private List<String> publishTimeRange;
    /*** 文章分类 */
    private String categoryId;
    /*** 文章内容 */
    private String content;
    /*** 审核状态 */
    private Integer auditStatus;
    /*** 审核状态 范围查询*/
    @TableField(exist = false)
    private Integer[] auditStatusRange;
    /*** 审核结果 */
    private String auditResult;
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPicture() {
        return this.picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    public Date getPublishTime() {
        return this.publishTime;
    }
    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }
    public List<String> getPublishTimeRange(){
        return this.publishTimeRange;
    }
    public void setPublishTimeRange(List<String> publishTimeRange){
        this.publishTimeRange = publishTimeRange;
    }
    public String getCategoryId() {
        return this.categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Integer getAuditStatus() {
        return this.auditStatus;
    }
    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }
    public Integer[] getAuditStatusRange(){
        return this.auditStatusRange;
    }
    public void setAuditStatusRange(Integer[] auditStatusRange){
        this.auditStatusRange = auditStatusRange;
    }
    public String getAuditResult() {
        return this.auditResult;
    }
    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }
}