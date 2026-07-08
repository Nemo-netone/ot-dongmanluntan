package com.admin.sys.base.scheduler;
import com.admin.sys.base.exception.AppException;
import com.admin.sys.base.module.quartz.entity.SysQuartz;
import com.admin.sys.base.module.quartz.service.SysQuartzService;
import com.admin.sys.utils.admin.SpringContextHolder;
import com.admin.sys.utils.admin.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 定时任务
 *
 * @author liaogw
 * @date 2016年11月30日 下午12:44:21
 */
public class ScheduleExecute extends QuartzJobBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private SysQuartzService sysQuartzService = SpringContextHolder.getBean("sysQuartzService");

    @Override
    protected void executeInternal(JobExecutionContext context) {
        SysQuartz sysQuartz = (SysQuartz) context.getMergedJobDataMap().get(SysQuartz.JOB_PARAM_KEY);
        try {
            sysQuartz.setLastExecTime(new Date());
            String beanName = sysQuartz.getBeanName();
            String methodName = sysQuartz.getMethodName();
            String params = sysQuartz.getParams();
            Object bean = SpringContextHolder.getBean(beanName);
            if (sysQuartz.getHasParams() == 1 && StringUtils.isBlank(params)) {//有参方法却没传递参数
                throw new AppException("有参方法未设置参数");
            }
            if (sysQuartz.getHasParams() == 0 && StringUtils.isNotBlank(params)) {//无参方法设置了参数
                throw new AppException("无参方法设置了参数");
            }
            if (StringUtils.isNotBlank(params)) {//有参方法且有参数传递
                Method method = bean.getClass().getDeclaredMethod(methodName, String.class);
                method.invoke(bean, params);
            } else {
                Method method = bean.getClass().getDeclaredMethod(methodName);
                method.invoke(bean);
            }
            sysQuartz.setJobStatus(1);//执行成功
        } catch (NoSuchBeanDefinitionException e) {
            sysQuartz.setJobStatus(2);//没找到实例
            ScheduleManager.removeJob(sysQuartz.getId());
            logger.error("任务执行失败，任务ID：" + sysQuartz.getId(), e);
        } catch (NoSuchMethodException e) {
            sysQuartz.setJobStatus(3);//没找到方法
            ScheduleManager.removeJob(sysQuartz.getId());
            logger.error("任务执行失败，任务ID：" + sysQuartz.getId(), e);
        } catch (AppException e) {
            sysQuartz.setJobStatus(4);//方法和参数不匹配
            ScheduleManager.removeJob(sysQuartz.getId());
        } catch (IllegalAccessException e) {
            sysQuartz.setJobStatus(10);//其他异常
            ScheduleManager.removeJob(sysQuartz.getId());
        } catch (InvocationTargetException e) {
            sysQuartz.setJobStatus(10);//其他异常
            ScheduleManager.removeJob(sysQuartz.getId());
        } finally {
            try {
                sysQuartzService.update(sysQuartz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
