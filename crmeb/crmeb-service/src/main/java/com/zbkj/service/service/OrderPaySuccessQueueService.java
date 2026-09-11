package com.zbkj.service.service;

/**
 * 订单支付成功后置任务队列服务
 */
public interface OrderPaySuccessQueueService {

    /**
     * 将订单加入兜底队列，并立即触发异步处理。
     *
     * @param orderNo 订单编号
     * @return 是否成功加入队列
     */
    Boolean enqueue(String orderNo);
}
