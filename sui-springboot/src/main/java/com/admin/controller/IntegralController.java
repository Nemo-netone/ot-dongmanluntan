package com.admin.controller;

import com.admin.service.IntegralService;
import com.admin.entity.Integral;
import java.util.List;
import java.util.ArrayList;
import com.admin.sys.base.entity.ResultInfo;
import com.admin.sys.base.entity.DateUtils;
import com.admin.sys.utils.admin.StringUtils;
import com.admin.sys.utils.excel.ExportExcel;
import com.admin.sys.utils.excel.ImportExcel;
import javax.servlet.http.HttpServletResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.multipart.MultipartFile;
import com.admin.sys.base.module.extend.web.BaseController;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
*【积分信息】页面接口
*/
@RestController
@RequestMapping("/admin/integral")
public class IntegralController extends BaseController {

    @Autowired
    private IntegralService integralService;

    /**
    * 【积分信息】根据条件分页查询
    * @param page
    * @param integral
    * @return
    */
    @RequestMapping("/getPage")
    public ResultInfo getPage(Page<Integral> page, Integral integral) {
        IPage<Integral> iPage = integralService.getPage(page, integral);
        return ResultInfo.ok("获取分页成功", iPage);
    }

    /**
    * 【积分信息】根据条件查询
    * @param integral
    * @return
    */
    @RequestMapping("/getList")
    public ResultInfo getList(Integral integral) {
        List<Integral> list = integralService.getList(integral);
        return ResultInfo.ok("获取列表成功", list);
    }

    /**
    * 【积分信息】根据对象查询
    * @param integral
    * @return
    */
    @RequestMapping("/get")
    public ResultInfo get(Integral integral) {
        Integral entity = integralService.get(integral);
        return ResultInfo.ok("获取对象成功", entity);
    }

    /**
    * 【积分信息】提交(新增或修改)
    * @param integral
    * @return
    */
    @RequestMapping("/sub")
    public ResultInfo insert(Integral integral) {
        if (StringUtils.isEmpty(integral.getId())) { //新增
            integralService.insert(integral);
        } else {//修改
            integralService.update(integral);
        }
        return ResultInfo.ok("提交成功!");
    }

    /**
    * 【积分信息】删除
    * @param id
    * @return
    */
    @RequestMapping("/delete")
    public ResultInfo delete(String id) {
        integralService.delete(id);
        return ResultInfo.ok("删除成功!");
    }

    /**
    * 【积分信息】批量删除
    * @param ids
    * @return
    */
    @RequestMapping("/delAll")
    public ResultInfo delAll(String[] ids) {
        integralService.delAll(ids);
        return ResultInfo.ok("批量删除成功！");
    }



}



