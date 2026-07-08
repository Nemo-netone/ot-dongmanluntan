package com.admin.sys.base.module.quartz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.admin.sys.base.module.extend.service.BaseService;
import com.admin.sys.base.module.quartz.entity.SysQuartz;
import com.admin.sys.base.module.quartz.dao.SysQuartzDao;
import com.admin.sys.utils.admin.ObjectUtils;
import com.admin.sys.utils.admin.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

//任务信息接口类
@Service
public class SysQuartzService extends BaseService {

    @Autowired
    private SysQuartzDao sysQuartzDao;

    //【任务信息】设置查询条件
    private LambdaQueryWrapper<SysQuartz> getSysQuartzQueryCondition(SysQuartz sysQuartz) {
        LambdaQueryWrapper<SysQuartz> lambdaQuery = this.getBaseQueryCondition(sysQuartz);
        //根据创建时间排序
        lambdaQuery.orderByDesc(SysQuartz::getCreateDate);
        if (ObjectUtils.isNotNull(sysQuartz)) {
            //【主键id】精确查询
            lambdaQuery.eq(StringUtils.isNotEmpty(sysQuartz.getId()), SysQuartz::getId, sysQuartz.getId());
            //【名称】模糊查询
            lambdaQuery.like(StringUtils.isNotEmpty(sysQuartz.getJobName()), SysQuartz::getJobName, sysQuartz.getJobName());
            //【目标对象】模糊查询
            lambdaQuery.like(StringUtils.isNotEmpty(sysQuartz.getBeanName()), SysQuartz::getBeanName, sysQuartz.getBeanName());
            //【参数】模糊查询
            lambdaQuery.like(StringUtils.isNotEmpty(sysQuartz.getParams()), SysQuartz::getParams, sysQuartz.getParams());
            //【表达式】模糊查询
            lambdaQuery.like(StringUtils.isNotEmpty(sysQuartz.getCronExpression()), SysQuartz::getCronExpression, sysQuartz.getCronExpression());
            //【类型】精确查询
            lambdaQuery.eq(StringUtils.isNotNull(sysQuartz.getJobType()), SysQuartz::getJobType, sysQuartz.getJobType());
            //【是否有参数】精确查询
            lambdaQuery.eq(StringUtils.isNotNull(sysQuartz.getHasParams()), SysQuartz::getHasParams, sysQuartz.getHasParams());
            //【目标方法】模糊查询
            lambdaQuery.like(StringUtils.isNotEmpty(sysQuartz.getMethodName()), SysQuartz::getMethodName, sysQuartz.getMethodName());
            //【状态】精确查询
            lambdaQuery.eq(StringUtils.isNotNull(sysQuartz.getJobStatus()), SysQuartz::getJobStatus, sysQuartz.getJobStatus());
            //【是否执行】精确查询
            lambdaQuery.eq(StringUtils.isNotNull(sysQuartz.getIsExec()), SysQuartz::getIsExec, sysQuartz.getIsExec());
            //【最后执行时间】精确查询
            lambdaQuery.eq(StringUtils.isNotNull(sysQuartz.getLastExecTime()), SysQuartz::getLastExecTime, sysQuartz.getLastExecTime());
        }
        return lambdaQuery;
    }

    //【任务信息】分页查询
    public IPage<SysQuartz> getPage(Page<SysQuartz> page, SysQuartz sysQuartz) {
        LambdaQueryWrapper<SysQuartz> lambdaQuery = getSysQuartzQueryCondition(sysQuartz);
        return sysQuartzDao.selectPage(page, lambdaQuery);
    }
    
    //【任务信息】查询列表
    public List<SysQuartz> getList(SysQuartz sysQuartz) {
        LambdaQueryWrapper<SysQuartz> lambdaQuery = getSysQuartzQueryCondition(sysQuartz);
        return sysQuartzDao.selectList(lambdaQuery);
    }
    
    //【任务信息】根据id查询
    public SysQuartz get(String id) {
        return sysQuartzDao.selectById(id);
    }
    
    //【任务信息】新增
    public int insert(SysQuartz sysQuartz) {
        this.preInsert(sysQuartz);
        return sysQuartzDao.insert(sysQuartz);
    }
    
    //【任务信息】修改
    public int update(SysQuartz sysQuartz) {
        this.preUpdate(sysQuartz);
        return sysQuartzDao.updateById(sysQuartz);
    }
    
    //【任务信息】删除
    public int delete(String id) {
        return sysQuartzDao.deleteById(id);
    }

    //【任务信息】批量删除
    public int delAll(String[] ids) {
        return sysQuartzDao.deleteBatchIds(Arrays.asList(ids));
    }

}