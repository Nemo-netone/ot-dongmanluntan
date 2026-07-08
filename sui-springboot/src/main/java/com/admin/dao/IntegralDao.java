package com.admin.dao;

import com.admin.entity.Integral;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 【积分信息】数据接口
* IntegralDao与IntegralDaoMapper.xml进行接口映射
* 用于扩展自定义sql方法
*/
@Repository
public interface IntegralDao extends BaseMapper<Integral> {

}

