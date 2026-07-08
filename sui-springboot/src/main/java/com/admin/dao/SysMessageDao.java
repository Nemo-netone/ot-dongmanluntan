package com.admin.dao;

import com.admin.entity.SysMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* 【消息信息】数据接口
* SysMessageDao与SysMessageDaoMapper.xml进行接口映射
* 用于扩展自定义sql方法
*/
@Repository
public interface SysMessageDao extends BaseMapper<SysMessage> {

}

