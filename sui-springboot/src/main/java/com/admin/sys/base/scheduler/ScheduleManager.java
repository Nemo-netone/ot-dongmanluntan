package com.admin.sys.base.scheduler;
import com.admin.sys.base.exception.AppException;
import com.admin.sys.base.module.quartz.entity.SysQuartz;
import com.admin.sys.utils.admin.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.admin.sys.base.module.config.service.SysConfigService.getSysConfig;

public class ScheduleManager {
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    private static Scheduler scheduler;
    private static final Logger logger = LoggerFactory.getLogger(ScheduleManager.class);

    static {
        try {
            scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private final static String JOB_NAME = "TASK_";

    /**
     * @param id
     * @throws SchedulerException
     * @Description: 移除一个任务(使用默认的任务组名 ， 触发器名 ， 触发器组名)
     */
    public static void removeJob(String id) {
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobKey jobKey = getJobKey(id);
            // 停止触发器
            scheduler.pauseJob(jobKey);
            scheduler.unscheduleJob(getTriggerKey(id));// 移除触发器
            scheduler.deleteJob(jobKey);// 删除任务
        } catch (SchedulerException e) {
            throw new AppException(e.getMessage());
        }
    }

    /**
     * 启动所有任务
     *
     * @throws SchedulerException
     */
    public static void startJobs() throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        logger.info("启动所有任务");
    }

    /**
     * 关闭所有定时任务
     *
     * @throws SchedulerException
     */
    public static void shutdownJobs() throws SchedulerException {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
            logger.info("关闭所有任务");
        }
    }


    //************************************************************8

    /**
     * 获取触发器key
     */
    public static TriggerKey getTriggerKey(String jobId) {
        return TriggerKey.triggerKey(JOB_NAME + jobId);
    }

    /**
     * 获取jobKey
     */
    public static JobKey getJobKey(String jobId) {
        return JobKey.jobKey(JOB_NAME + jobId);
    }


    /**
     * 获取触发器
     * @param id
     * @return
     */
    public static CronTrigger getCronTrigger(String id) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(id));
        } catch (SchedulerException e) {
            logger.info("获取定时任务CronTrigger出现异常" + e.getMessage());
            return null;
        }
    }

    /**
     * 创建job任务
     * @param sysQuartz
     * @throws SchedulerException
     */
    public static void createScheduleJob(SysQuartz sysQuartz) throws SchedulerException {
        // 用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
        JobDetail jobDetail = JobBuilder.newJob(ScheduleExecute.class)
                .withIdentity(sysQuartz.getId()).build();
        // 构建一个触发器，规定触发的规则
        Trigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder来规范一个触发器
                .withIdentity(getTriggerKey(sysQuartz.getId()))// 给触发器起一个名字和组名
                .startNow()// 立即执行
                .withSchedule(CronScheduleBuilder.cronSchedule(sysQuartz.getCronExpression())) // 触发器的执行时间
                .build();// 产生触发器
        trigger.getJobDataMap().put(SysQuartz.JOB_PARAM_KEY, sysQuartz);
        scheduler.scheduleJob(jobDetail, trigger);
        // 启动
        if (!scheduler.isShutdown()) {
            scheduler.start();
        }
    }

    /**
     * 更新job任务
     * @param scheduleJob
     * @throws SchedulerException
     */
    public static void updateScheduleJob(SysQuartz scheduleJob) throws SchedulerException {
        if (StringUtils.isNotEmpty(scheduleJob.getId())) {
            TriggerKey triggerKey = getTriggerKey(scheduleJob.getId());
            CronTrigger trigger = getCronTrigger(scheduleJob.getId());
            if (trigger != null) {
                // Trigger已存在，更新相应的定时设置
                // 表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder()
                        .forJob(scheduleJob.getId())
                        .withIdentity(triggerKey)
                        .startNow()
                        .withSchedule(scheduleBuilder).build();
                // 按新的trigger重新设置job执行
                trigger.getJobDataMap().put(scheduleJob.JOB_PARAM_KEY, scheduleJob);
                scheduler.rescheduleJob(triggerKey, trigger);
            } else {//对异常移除后的修改得重新加入管理
                createScheduleJob(scheduleJob);
            }

        }
    }

    /**
     * 创建或更新任务
     */
    public static void createOrUpdateScheduleJob(SysQuartz scheduleJob) {
        try {
            boolean isScheduleOpen = "1".equals(getSysConfig("isScheduleOpen"));
            if (!isScheduleOpen) {
                throw new AppException("您还未开启全局定时任务");
            }
            CronTrigger cronTrigger = ScheduleManager.getCronTrigger(scheduleJob.getId());
            //如果不存在，则创建
            if (cronTrigger == null) {
                ScheduleManager.createScheduleJob(scheduleJob);
            } else {//如果存在
                ScheduleManager.updateScheduleJob(scheduleJob);
            }
        } catch (SchedulerException e) {
            throw new AppException(e.getMessage());
        }
    }
}