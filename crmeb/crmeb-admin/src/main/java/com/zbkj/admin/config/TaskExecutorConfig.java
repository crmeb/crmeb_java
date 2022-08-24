package com.zbkj.admin.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Task类的线程配置
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@Configuration
public class TaskExecutorConfig {

    //普通模式
    private final int taskCorePoolSize = 20; //核心线程池数量
    private final int taskMaxPoolSize = 100; //最大线程
    private final int taskQueueCapacity = 200; //缓存队列条数
    private final int taskKeepAliveSecond = 10; //允许的空闲时间
    private final String taskNamePrefix = "task-executor-"; //线程名称前缀

    //发布订阅模式
//    private final int listenerCorePoolSize = 3;
//    private final int listenerMaxPoolSize = 20;
//    private final int listenerQueueCapacity = 200;
//    private final int listenerKeepAliveSecond = 10;
//    private final String listenerNamePrefix = "listener-executor-";

    //普通模式
    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor(){
        return initTaskExecutor(
                getTaskCorePoolSize(),
                getTaskMaxPoolSize(),
                getTaskQueueCapacity(),
                getTaskKeepAliveSecond(),
                getTaskNamePrefix()
        );
    }

//    //针对发布订阅（pub listener） 的线程池
//    @Bean("listenerTaskExecutor")
//    public ThreadPoolTaskExecutor listenerTaskExecutor(){
//        return initTaskExecutor(getListenerCorePoolSize(), getListenerMaxPoolSize(),
//                getListenerQueueCapacity(), getListenerKeepAliveSecond(), getListenerNamePrefix());
//    }

    /**
     *
     * 初始化TaskExecutor
     * @param corePoolSize  int 默认线程数
     * @param maxPoolSize   int 最大线程数
     * @param queueCapacity int 缓冲队列长度
     * @param keepAliveSecond   int 允许空闲时间
     * @param namePrefix    String  名称的前缀
     *
     * @return ThreadPoolTaskExecutor
     */
    private ThreadPoolTaskExecutor initTaskExecutor(int corePoolSize, int maxPoolSize,
                                                    int queueCapacity, int keepAliveSecond, String namePrefix){
        //callrunspolicy：由调度线程(提交任务的线程)处理该任务CallerRunsPolicy
        return initTaskExecutor(corePoolSize, maxPoolSize, queueCapacity, keepAliveSecond, namePrefix,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 初始化TaskExecutor
     * @param corePoolSize  int 默认线程数
     * @param maxPoolSize   int 最大线程数
     * @param queueCapacity int 缓冲队列长度
     * @param keepAliveSecond   int 允许空闲时间
     * @param namePrefix    String  名称的前缀
     * @param rejectedExecutionHandler  线程池满的时候如何处理
     * @return ThreadPoolTaskExecutor
     */
    private ThreadPoolTaskExecutor initTaskExecutor(int corePoolSize, int maxPoolSize,
                                                    int queueCapacity, int keepAliveSecond, String namePrefix,
                                                    RejectedExecutionHandler rejectedExecutionHandler){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);//核心线程数(默认线程数)
        executor.setMaxPoolSize(maxPoolSize);//最大线程数
        executor.setQueueCapacity(queueCapacity);//缓冲队列数
        executor.setKeepAliveSeconds(keepAliveSecond);//允许线程空闲时间(单位默认为秒)
        executor.setThreadNamePrefix(namePrefix);//线程名前缀

        //线程池对拒绝任务的处理策略，
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);

        //初始化
        executor.initialize();
        return executor;
    }
}
