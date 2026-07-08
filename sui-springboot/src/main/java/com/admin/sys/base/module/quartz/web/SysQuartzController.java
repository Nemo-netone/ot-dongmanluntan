package com.admin.sys.base.module.quartz.web;

import com.admin.sys.base.module.extend.web.BaseController;
import com.admin.sys.base.entity.DateUtils;
import com.admin.sys.base.entity.ResultInfo;
import com.admin.sys.base.exception.AppException;
import com.admin.sys.base.scheduler.ScheduleManager;
import com.admin.sys.base.module.quartz.entity.SysQuartz;
import com.admin.sys.base.module.quartz.service.SysQuartzService;
import com.admin.sys.utils.admin.StringUtils;
import com.admin.sys.utils.excel.ExportExcel;
import com.admin.sys.utils.excel.ImportExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import static com.admin.sys.base.scheduler.ScheduleManager.createOrUpdateScheduleJob;
import static com.admin.sys.base.module.config.service.SysConfigService.getSysConfig;

/**
*【任务信息】页面接口
*/
@RestController
@RequestMapping("/admin/sysQuartz")
public class SysQuartzController extends BaseController {

    @Autowired
    private SysQuartzService sysQuartzService;

    /**
    * 【任务信息】根据条件分页查询
    * @param page
    * @param sysQuartz
    * @return
    */
    @RequestMapping("/getPage")
    @RequiresPermissions("sysQuartz:getPage")
    public ResultInfo getPage(Page<SysQuartz> page, SysQuartz sysQuartz) {
        sysQuartz.setDelFlag(0);
        IPage<SysQuartz> iPage = sysQuartzService.getPage(page, sysQuartz);
        return ResultInfo.ok("获取分页成功", iPage);
    }

    /**
    * 【任务信息】根据条件查询
    * @param sysQuartz
    * @return
    */
    @RequestMapping("/getList")
    public ResultInfo getList(SysQuartz sysQuartz) {
        sysQuartz.setDelFlag(0);
        List<SysQuartz> list = sysQuartzService.getList(sysQuartz);
        return ResultInfo.ok("获取列表成功", list);
    }

    /**
    * 【任务信息】根据id查询
    * @param id
    * @return
    */
    @RequestMapping("/get")
    @RequiresPermissions(value = {"sysQuartz:edit","sysQuartz:view"},logical = Logical.OR)
    public ResultInfo get(String id) {
        SysQuartz sysQuartz = sysQuartzService.get(id);
        return ResultInfo.ok("获取对象成功", sysQuartz);
    }

    /**
    * 【任务信息】提交(新增或修改)
    * @param sysQuartz
    * @return
    */
    @RequestMapping("/sub")
    @RequiresPermissions("sysQuartz:save")
    public ResultInfo insert(SysQuartz sysQuartz) {
        if (StringUtils.isEmpty(sysQuartz.getId())) { //新增
            sysQuartzService.insert(sysQuartz);
        } else {//修改
            sysQuartzService.update(sysQuartz);
        }
        return ResultInfo.ok("提交成功!");
    }
    /**
    * 开启|关闭
    * @param sysQuartz
    * @return
    */
    @RequestMapping("/updateSchedule")
    @RequiresPermissions("sysQuartz:save")
    @Transactional
    public ResultInfo updateSchedule(SysQuartz sysQuartz) {
        boolean isScheduleOpen = "1".equals(getSysConfig("isScheduleOpen"));
        if (!isScheduleOpen) {
            throw new AppException("您还未开启全局定时任务");
        }
        //更新容器
        sysQuartzService.update(sysQuartz);
        if (sysQuartz.getIsExec() == 0) {//停止
            ScheduleManager.removeJob(sysQuartz.getId());
        } else {
            createOrUpdateScheduleJob(sysQuartz);
        }
        return ResultInfo.ok("提交成功!");
    }

    /**
    * 【任务信息】删除
    * @param id
    * @return
    */
    @RequestMapping("/delete")
    @RequiresPermissions("sysQuartz:delete")
    @Transactional
    public ResultInfo delete(String id) {
        ScheduleManager.removeJob(id);
        sysQuartzService.delete(id);
        return ResultInfo.ok("删除成功!");
    }

    /**
     * 【任务信息】批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delAll")
    @RequiresPermissions("sysQuartz:delete")
    public ResultInfo delAll(String[] ids) {
        for (String id : ids) {
            ScheduleManager.removeJob(id);
        }
        sysQuartzService.delAll(ids);
        return ResultInfo.ok("批量删除成功！");
    }

    /**
    *【任务信息】导出
    */
    @RequestMapping(value = "/export")
    @RequiresPermissions("sysQuartz:export")
    public void exportFile(SysQuartz sysQuartz, HttpServletResponse response) {
        try {
            String fileName = "任务信息" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            List<SysQuartz> list = sysQuartzService.getList(sysQuartz);
            new ExportExcel("任务信息", SysQuartz.class).setDataList(list).write(response, fileName).dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    *【任务信息】导入
    */
    @RequestMapping(value = "/import")
    @RequiresPermissions("sysQuartz:import")
    public ResultInfo importFile(MultipartFile file) {
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<SysQuartz> list = ei.getDataList(SysQuartz.class);
            for (SysQuartz sysQuartz : list) {
                try {
                    sysQuartzService.insert(sysQuartz);
                    successNum++;
                } catch (Exception ex) {
                    failureNum++;
                }
            }
            if (failureNum > 0) {
                failureMsg.insert(0, "，失败 " + failureNum + " 条任务信息记录。");
            }
            return ResultInfo.ok("已成功导入 " + successNum + " 条任务信息记录" + failureMsg);
        } catch (Exception e) {
            return ResultInfo.error("导入任务信息失败！失败信息：" + e.getMessage());
        }
    }

    /**
    *【任务信息】模板下载
    */
    @RequestMapping(value = "/import/template")
    @RequiresPermissions("sysQuartz:import")
    public ResultInfo importFileTemplate(HttpServletResponse response) {
        try {
            String fileName = "任务信息数据导入模板.xlsx";
            List<SysQuartz> list = new ArrayList<>();
            new ExportExcel("任务信息数据", SysQuartz.class, 1).setDataList(list).write(response, fileName).dispose();
            return ResultInfo.ok("下载模板成功！");
        } catch (Exception e) {
            return ResultInfo.error("导入模板下载失败！失败信息：" + e.getMessage());
        }
    }


}



