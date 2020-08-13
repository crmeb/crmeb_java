package com.zbkj.crmeb.store.service;


 /**
 * 订单任务服务
 * @author Mr.Zhang
 * @description StoreOrderService 接口
 * @date 2020-05-28
 */
 public interface OrderTaskService{

     void cancelByUser();

     void refundApply();

     void complete();

     void takeByUser();

     void deleteByUser();
 }
