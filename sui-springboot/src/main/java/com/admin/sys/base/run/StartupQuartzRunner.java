package com.admin.sys.base.run;

import com.admin.sys.base.module.quartz.entity.SysQuartz;
import com.admin.sys.base.module.quartz.service.SysQuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.List;
import static com.admin.sys.base.scheduler.ScheduleManager.createOrUpdateScheduleJob;
import static com.admin.sys.base.module.config.service.SysConfigService.getSysConfig;

/**
 * 服务启动后执行：缓存加载后加载定时任务
 */
@Component
@Order(value = 2)
public class StartupQuartzRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(StartupCacheRunner.class);
    @Autowired
    private SysQuartzService sysQuartzService;

    @Override
    public void run(String... args) {
        boolean isScheduleOpen = "1".equals(getSysConfig("isScheduleOpen"));
        if (isScheduleOpen) {
            logger.info(">>>>>>>>>>>>>>> 【开启定时任务】 <<<<<<<<<<<<<");
            SysQuartz sysQuartz = new SysQuartz();
            sysQuartz.setDelFlag(0);
            List<SysQuartz> scheduleJobList = sysQuartzService.getList(sysQuartz);
            if (scheduleJobList.size() > 0) {
                for (SysQuartz scheduleJob : scheduleJobList) {
                    //获取并启动任务
                    scheduleJob.setIsExec(1);//设置开启
                    createOrUpdateScheduleJob(scheduleJob);
                }
            }
        }
    }
}