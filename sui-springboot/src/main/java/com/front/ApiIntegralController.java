package com.front;

import com.admin.entity.Register;
import com.admin.service.IntegralService;
import com.admin.entity.Integral;

import java.util.*;
import java.util.stream.Collectors;

import com.admin.service.RegisterService;
import com.admin.sys.base.entity.ResultInfo;
import com.admin.sys.utils.admin.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.admin.sys.base.module.extend.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
*【积分信息】管理
*/
@RestController
@RequestMapping("/api/integral")
public class ApiIntegralController extends BaseController {

    @Autowired
    private IntegralService integralService;
    @Autowired
    private RegisterService registerService;

    /**
     *【积分信息】获取分页数据
     */
    @RequestMapping("/getPage")
    public ResultInfo getPage(Page<Integral> page, Integral integral) {
        IPage<Integral> iPage = integralService.getPage(page, integral);
        return ResultInfo.ok("获取分页成功",iPage);
    }

    /**
     *【积分信息】获取排行数据
     */
    @RequestMapping("/getRankingList")
    public ResultInfo getRankingPage(Integral integral) {
        List<Integral> list = integralService.getList(integral);
        Map<String, IntSummaryStatistics> collect = list.stream().collect(Collectors.groupingBy(Integral::getRegisterId, Collectors.summarizingInt(Integral::getIntegralNum)));
        List<Integral> rankingList = new ArrayList<>();
        for (Map.Entry<String, IntSummaryStatistics> entry : collect.entrySet()) {
            Integral entity = new Integral();
            Register register = registerService.get(entry.getKey());
            entity.setName(register.getUserName()+"的积分");
            entity.setRegisterId(entry.getKey());
            entity.setIntegralNum(Integer.valueOf(entry.getValue().getSum()+""));
            entity.setIntegralTime(new Date());
            entity.setRemarks(register.getUserName()+"获得的总积分");
            rankingList.add(entity);
        }
        List<Integral> sortList = rankingList.stream().sorted(Comparator.comparing(Integral::getIntegralNum).reversed()).collect(Collectors.toList());
        return ResultInfo.ok("获取列表成功", sortList);
    }

    /**
    * 【积分信息】获取列表数据
    */
    @RequestMapping("/getList")
    public ResultInfo getList(Integral integral) {
        List<Integral> list = integralService.getList(integral);
        return ResultInfo.ok("获取列表成功", list);
    }

    /**
    *【积分信息】根据对象数据
    */
    @RequestMapping("/get")
    public ResultInfo get(Integral integral) {
        Integral entity = integralService.get(integral);
        return ResultInfo.ok("获取成功",entity);
    }

    /**
     *【积分信息】保存或修改
     */
    @RequestMapping("/sub")
    public ResultInfo saveOrUpdate(HttpServletRequest request,Integral integral){
        try {
            if(StringUtils.isEmpty(integral.getId())){
                integralService.insert(integral);//新增
                return ResultInfo.ok("保存成功！");
            }else{
                integralService.update(integral);//修改
                return ResultInfo.ok("修改成功！");
            }
        } catch (Exception e) {
            return ResultInfo.error("保存失败！失败信息:"+e.getMessage());
        }
    }

    /**
     *【积分信息】根据id删除
     */
    @RequestMapping("/delete")
    public ResultInfo delete(HttpServletRequest request, Integral integral) {
        integralService.delete(integral.getId());
        return ResultInfo.ok("删除成功！");
    }

    /**
    * 【积分信息】批量删除
    */
    @RequestMapping("/delAll")
    public ResultInfo delAll(String[] ids) {
        integralService.delAll(ids);
        return ResultInfo.ok("批量删除成功！");
    }

}



