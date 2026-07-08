package com.admin.service;

import com.admin.entity.Register;
import com.admin.entity.SysUser;
import com.admin.sys.base.module.extend.service.BaseService;
import com.admin.dao.SysMessageDao;
import com.admin.entity.SysMessage;
import com.admin.sys.utils.admin.ObjectUtils;
import com.admin.sys.utils.admin.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

//消息信息接口类
@Service
public class SysMessageService extends BaseService {

    @Autowired
    private SysMessageDao sysMessageDao;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private SysUserService sysUserService;

    //【消息信息】设置查询条件
    private LambdaQueryWrapper<SysMessage> getSysMessageQueryCondition(SysMessage sysMessage) {
        LambdaQueryWrapper<SysMessage> lambdaQuery = this.getBaseQueryCondition(sysMessage);
        lambdaQuery.orderByAsc(SysMessage::getId);
        if (ObjectUtils.isNotNull(sysMessage)) {
            //【主键id】精确查询
            lambdaQuery.eq(StringUtils.isNotEmpty(sysMessage.getId()), SysMessage::getId, sysMessage.getId());
            //【消息类型】精确查询
            lambdaQuery.eq(StringUtils.isNotEmpty(sysMessage.getType()), SysMessage::getType, sysMessage.getType());
            //【父级id】模糊查询
            lambdaQuery.eq(StringUtils.isNotEmpty(sysMessage.getPid()), SysMessage::getPid, sysMessage.getPid());
            //【用户id】模糊查询
            lambdaQuery.eq(StringUtils.isNotEmpty(sysMessage.getUserId()), SysMessage::getUserId, sysMessage.getUserId());
            //【关联id】模糊查询
            lambdaQuery.eq(StringUtils.isNotEmpty(sysMessage.getRefId()), SysMessage::getRefId, sysMessage.getRefId());
            //【星级】精确查询
            lambdaQuery.eq(StringUtils.isNotNull(sysMessage.getStars()), SysMessage::getStars, sysMessage.getStars());
        }
        return lambdaQuery;
    }

    //【消息信息】分页查询
    public IPage<SysMessage> getPage(Page<SysMessage> page, SysMessage sysMessage) {
        LambdaQueryWrapper<SysMessage> lambdaQuery = getSysMessageQueryCondition(sysMessage);
        return sysMessageDao.selectPage(page, lambdaQuery);
    }
    //【消息信息】分页查询
    public IPage<SysMessage> getTreePage(Page<SysMessage> page, SysMessage sysMessage) {
        LambdaQueryWrapper<SysMessage> lambdaQuery = getSysMessageQueryCondition(sysMessage);
        Page<SysMessage> iPage = sysMessageDao.selectPage(page, lambdaQuery);
        List<SysMessage> records = iPage.getRecords();
        List<SysMessage> messageChildren = this.getMessageChildren(records);
        iPage.setRecords(messageChildren);
        return iPage;
    }

    //消息数据递归
    public List<SysMessage> getMessageChildren(List<SysMessage> allList) {
        allList.forEach(item -> {
            SysMessage entity = new SysMessage();
            entity.setPid(item.getId());
            Register register = registerService.get(item.getUserId());
            setInfo(item, register);
            List<SysMessage> children = this.getList(entity);
            if(children.size()>0){
                item.setChildren(children);
            }
            if (item.getChildren() != null && item.getChildren().size() > 0) {//递归
                getMessageChildren(item.getChildren());
            }
        });
        return allList;
    }

    //【消息信息】查询列表
    public List<SysMessage> getList(SysMessage sysMessage) {
        LambdaQueryWrapper<SysMessage> lambdaQuery = getSysMessageQueryCondition(sysMessage);
        return sysMessageDao.selectList(lambdaQuery);
    }

    //获取用户信息
    public void setInfo(SysMessage message, Register register) {
        if (ObjectUtils.isNotNull(register)) {
            message.setPhoto(register.getPhoto());
            message.setLoginName(register.getLoginName());
            message.setUserName(register.getUserName());
            message.setEmail(register.getEmail());
        } else {
            SysUser sysUser = sysUserService.get(message.getUserId());
            if (ObjectUtils.isNotNull(sysUser)) {
                message.setPhoto(sysUser.getPhoto());
                message.setLoginName(sysUser.getLoginName());
                message.setUserName(sysUser.getUserName());
                message.setEmail(sysUser.getEmail());
            }
        }
    }

    //【消息信息】根据id查询
    public SysMessage get(String id) {
        return sysMessageDao.selectById(id);
    }
    //【消息信息】根据对象查询
    public SysMessage get(SysMessage sysMessage) {
        LambdaQueryWrapper<SysMessage> lambdaQuery = getSysMessageQueryCondition(sysMessage);
        return sysMessageDao.selectOne(lambdaQuery);
    }

    //【消息信息】新增
    public int insert(SysMessage sysMessage) {
        this.preInsert(sysMessage);
        return sysMessageDao.insert(sysMessage);
    }

    //【消息信息】修改
    public int update(SysMessage sysMessage) {
        this.preUpdate(sysMessage);
        return sysMessageDao.updateById(sysMessage);
    }

    //【消息信息】删除
    public int delete(String id) {
        return sysMessageDao.deleteById(id);
    }

    //【消息信息】批量删除
    public int delAll(String[] ids) {
        return sysMessageDao.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 获取收藏的推荐数据:收藏从高到低的商品
     * @param size:推荐个数
     * @return
     */
    public List<SysMessage> getCollectRecommendList(Integer size) {
        SysMessage sysMessage = new SysMessage();
        sysMessage.setType("收藏");
        List<SysMessage> collectList = this.getList(sysMessage);
        Map<String, List<SysMessage>> collect = collectList.stream().collect(Collectors.groupingBy(SysMessage::getRefId));
        List<SysMessage> collectArray = new ArrayList<>();
        //搜集收藏人数关联的商品数量
        for (Map.Entry<String, List<SysMessage>> entry : collect.entrySet()) {
            SysMessage message = new SysMessage();
            message.setRefId(entry.getKey());
            message.setStars(entry.getValue().size());
            collectArray.add(message);
        }
        //收藏人数从高到低排序
        List<SysMessage> sortCollectList = collectArray.stream().sorted(Comparator.comparing(SysMessage::getStars)).collect(Collectors.toList());
        System.out.println("商品收藏推荐如下：");
        for (SysMessage entity : sortCollectList) {
            System.out.println("商品id:【"+entity.getRefId()+"】收藏人数：【"+entity.getStars()+"】");
        }
        sortCollectList = sortCollectList.size()>size?sortCollectList.subList(0, size):sortCollectList;
        return sortCollectList;
    }

    /**
     * 获取评价的推荐数据:评分从高到低的商品
     * @param size:推荐个数
     * @return
     */
    public List<SysMessage> getEvaluateRecommendList(Integer size) {
        SysMessage sysMessage = new SysMessage();
        sysMessage.setType("评价");
        List<SysMessage> evaluateList = this.getList(sysMessage);
        Map<String, IntSummaryStatistics> collect = evaluateList.stream().collect(Collectors.groupingBy(SysMessage::getRefId, Collectors.summarizingInt(SysMessage::getStars)));
        List<SysMessage> evaluateArray = new ArrayList<>();
        //搜集评论分数关联的商品数量
        for (Map.Entry<String, IntSummaryStatistics> entry : collect.entrySet()) {
            SysMessage message = new SysMessage();
            message.setRefId(entry.getKey());
            message.setStars(Integer.valueOf(entry.getValue().getSum()+""));
            evaluateArray.add(message);
        }
        //评论分数从高到低排序
        List<SysMessage> sortEvaluateList = evaluateArray.stream().sorted(Comparator.comparing(SysMessage::getStars)).collect(Collectors.toList());
        System.out.println("商品评价推荐如下：");
        for (SysMessage entity : sortEvaluateList) {
            System.out.println("商品id:【"+entity.getRefId()+"】评价总分：【"+entity.getStars()+"】");
        }
        sortEvaluateList =sortEvaluateList.size()>size?sortEvaluateList.subList(0, size):sortEvaluateList;
        return sortEvaluateList;
    }

}