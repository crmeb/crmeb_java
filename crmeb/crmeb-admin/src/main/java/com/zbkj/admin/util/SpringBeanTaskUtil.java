package com.zbkj.admin.util;

import cn.hutool.core.util.StrUtil;
import com.zbkj.common.utils.SpringUtil;
import com.zbkj.admin.model.ScheduleJob;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 定时任务spring bean 执行定时任务
 * @Author 指缝de阳光
 * @Date 2021/11/30 17:00
 * @Version 1.0
 */
public class SpringBeanTaskUtil {

    public static void invokeMethod(ScheduleJob scheduleJob) {
        Object target = SpringUtil.getBean(scheduleJob.getBeanName());
        try {
            if (StrUtil.isNotEmpty(scheduleJob.getParams())) {
                Method method = target.getClass().getDeclaredMethod(scheduleJob.getMethodName(), String.class);
                ReflectionUtils.makeAccessible(method);
                method.invoke(target, scheduleJob.getParams());
            } else {
                Method method = target.getClass().getDeclaredMethod(scheduleJob.getMethodName());
                ReflectionUtils.makeAccessible(method);
                method.invoke(target);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("执行定时任务失败", e);
        }
    }

}
