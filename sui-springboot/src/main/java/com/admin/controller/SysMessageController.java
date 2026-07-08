package com.admin.controller;

import com.admin.entity.Register;
import com.admin.service.RegisterService;
import com.admin.sys.base.entity.ResultInfo;
import com.admin.sys.base.module.extend.web.BaseController;
import com.admin.entity.SysMessage;
import com.admin.service.SysMessageService;
import com.admin.sys.utils.admin.ShiroUtils;
import com.admin.sys.utils.admin.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
*【消息信息】页面接口
*/
@RestController
@RequestMapping("/admin/sysMessage")
public class SysMessageController extends BaseController {

    @Autowired
    private SysMessageService sysMessageService;
    @Autowired
    private RegisterService registerService;

    /**
    * 【消息信息】根据条件分页查询
    * @param page
    * @param sysMessage
    * @return
    */
    @RequestMapping("/getPage")
    public ResultInfo getPage(Page<SysMessage> page, SysMessage sysMessage) {
        sysMessage.setPid("0");
        IPage<SysMessage> iPage = sysMessageService.getPage(page, sysMessage);
        List<SysMessage> records = iPage.getRecords();
        for (SysMessage message : records) {
            Register register = registerService.get(message.getUserId());
            sysMessageService.setInfo(message, register);
            SysMessage params = new SysMessage();
            params.setPid(message.getId());
            List<SysMessage> childrens = sysMessageService.getList(params);
            for (SysMessage children : childrens) {
                Register reg = registerService.get(children.getUserId());
                sysMessageService.setInfo(children, reg);
            }
            message.setChildren(childrens);
        }
        return ResultInfo.ok("获取分页成功", iPage);
    }

    /**
    * 【消息信息】根据条件查询
    * @param sysMessage
    * @return
    */
    @RequestMapping("/getList")
    public ResultInfo getList(SysMessage sysMessage) {
        List<SysMessage> list = sysMessageService.getList(sysMessage);
        return ResultInfo.ok("获取列表成功", list);
    }

    /**
    * 【消息信息】根据id查询
    * @param id
    * @return
    */
    @RequestMapping("/get")
    public ResultInfo get(String id) {
        SysMessage sysMessage = sysMessageService.get(id);
        return ResultInfo.ok("获取对象成功", sysMessage);
    }

    /**
    * 【消息信息】提交(新增或修改)
    * @param sysMessage
    * @return
    */
    @RequestMapping("/sub")
    public ResultInfo insert(SysMessage sysMessage) {
        if (StringUtils.isEmpty(sysMessage.getId())) { //新增
            sysMessage.setUserId(ShiroUtils.getCurrentUserId());
            sysMessageService.insert(sysMessage);
        } else {//修改
            sysMessageService.update(sysMessage);
        }
        return ResultInfo.ok("提交成功!");
    }

    /**
    * 【消息信息】删除
    * @param id
    * @return
    */
    @RequestMapping("/delete")
    public ResultInfo delete(String id) {
        sysMessageService.delete(id);
        SysMessage params =new SysMessage();
        params.setPid(id);//删除子级信息
        List<SysMessage> list = sysMessageService.getList(params);
        for (SysMessage sysMessage : list) {
            sysMessageService.delete(sysMessage.getId());
        }
        return ResultInfo.ok("删除成功!");
    }

    /**
     * 【消息信息】批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delAll")
    public ResultInfo delAll(String[] ids) {
        sysMessageService.delAll(ids);
        for (String id : ids) {
            SysMessage params =new SysMessage();
            params.setPid(id);
            List<SysMessage> list = sysMessageService.getList(params);
            for (SysMessage sysMessage : list) {
                sysMessageService.delete(sysMessage.getId());
            }
        }
        return ResultInfo.ok("批量删除成功！");
    }


}



