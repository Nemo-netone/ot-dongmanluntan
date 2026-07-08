package com.admin.service;

import com.admin.dao.IntegralDao;
import com.admin.entity.Integral;
import java.util.List;
import java.util.Arrays;
import com.admin.sys.utils.admin.ObjectUtils;
import com.admin.sys.utils.admin.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.admin.sys.base.module.extend.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

//积分信息接口类
@Service
public class IntegralService extends BaseService {

    @Autowired
    private IntegralDao integralDao;

    //【积分信息】设置查询条件
    private LambdaQueryWrapper<Integral> getIntegralQueryCondition(Integral integral) {
        LambdaQueryWrapper<Integral> lambdaQuery = this.getBaseQueryCondition(integral);
        //根据创建时间排序
        lambdaQuery.orderByDesc(Integral::getCreateDate);
        if (ObjectUtils.isNotNull(integral)) {
            //【积分名称】模糊查询
            lambdaQuery.like(StringUtils.isNotEmpty(integral.getName()), Integral::getName, integral.getName());
            //【用户姓名】精确查询
            lambdaQuery.eq(StringUtils.isNotEmpty(integral.getRegisterId()), Integral::getRegisterId, integral.getRegisterId());
            //【积分时间】精确查询
            lambdaQuery.eq(ObjectUtils.isNotNull(integral.getIntegralTime()), Integral::getIntegralTime, integral.getIntegralTime());
            //【积分时间】范围查询
            if (ObjectUtils.isNotNull(integral.getIntegralTimeRange()) && integral.getIntegralTimeRange().size() > 0) {
                lambdaQuery.between(Integral::getIntegralTime, integral.getIntegralTimeRange().get(0), integral.getIntegralTimeRange().get(1));
            }
        }
        return lambdaQuery;
    }

    //【积分信息】分页查询
    public IPage<Integral> getPage(Page<Integral> page, Integral integral) {
        LambdaQueryWrapper<Integral> lambdaQuery = getIntegralQueryCondition(integral);
        return integralDao.selectPage(page, lambdaQuery);
    }
    
    //【积分信息】查询列表
    public List<Integral> getList(Integral integral) {
        LambdaQueryWrapper<Integral> lambdaQuery = getIntegralQueryCondition(integral);
        return integralDao.selectList(lambdaQuery);
    }
    
    //【积分信息】根据id查询
    public Integral get(String id) {
        return integralDao.selectById(id);
    }

    //【积分信息】根据对象查询
    public Integral get(Integral integral) {
    LambdaQueryWrapper<Integral> lambdaQuery = getIntegralQueryCondition(integral);
        return integralDao.selectOne(lambdaQuery);
    }
    
    //【积分信息】新增
    public int insert(Integral integral) {
        this.preInsert(integral);
        return integralDao.insert(integral);
    }
    
    //【积分信息】修改
    public int update(Integral integral) {
        this.preUpdate(integral);
        return integralDao.updateById(integral);
    }
    
    //【积分信息】删除
    public int delete(String id) {
        return integralDao.deleteById(id);
    }

    //【积分信息】批量删除
    public int delAll(String[] ids) {
        return integralDao.deleteBatchIds(Arrays.asList(ids));
    }

}