package com.admin.sys.base.module.quartz.jobs;

import com.admin.sys.base.module.extend.service.BaseService;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * 需要继承BaseService里的mybatis代理才能获取bean
 */
@Component
public class HelloJob extends BaseService {
    public void getUser() {
        System.out.println("进入任务***********无参方法:batStart*************");
        System.out.println(Calendar.getInstance().get(Calendar.SECOND));
    }
    public void getUser(String params) {
        System.out.println("进入任务***********有参方法:getUser("+params+")*************");
        System.out.println(Calendar.getInstance().get(Calendar.SECOND));
    }

}