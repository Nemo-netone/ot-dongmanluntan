package com.admin.sys.base.module.quartz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.admin.sys.base.module.quartz.entity.SysQuartz;
import org.springframework.stereotype.Repository;

/**
* 【任务信息】数据接口
* SysQuartzDao与SysQuartzDaoMapper.xml进行接口映射
* 用于扩展自定义sql方法
*/
@Repository
public interface SysQuartzDao extends BaseMapper<SysQuartz> {

}

