package com.admin.entity;

import com.admin.sys.base.module.extend.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

/**
*【消息信息】实体对象
*/

@TableName("sys_message")
public class SysMessage extends BaseEntity {
    /*** 主键id */
    private String id;
    /*** 消息类型 */
    private String type;
    /*** 父级id */
    private String pid;
    /*** 用户id */
    private String userId;
    /*** 关联id */
    private String refId;
    /*** 消息内容 */
    private String content;
    /*** 星级 */
    private Integer stars;
    /*** 地址 */
    private String url;
    /*** 头像 */
    @TableField(exist=false)
    private String photo;
    /*** 登录账号 */
    @TableField(exist=false)
    private String loginName;
    /*** 姓名 */
    @TableField(exist=false)
    private String userName;
    /*** 邮箱 */
    @TableField(exist=false)
    private String email;
    @TableField(exist=false)
    List<SysMessage> children;

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getPid() {
        return this.pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getRefId() {
        return this.refId;
    }
    public void setRefId(String refId) {
        this.refId = refId;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<SysMessage> getChildren() {
        return children;
    }

    public void setChildren(List<SysMessage> children) {
        this.children = children;
    }
}