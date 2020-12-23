package com.zbkj.crmeb.store.service;


/**
 * 订单任务服务 StoreOrderService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
 public interface OrderTaskService{

     void cancelByUser();

     void refundApply();

     void complete();

     void takeByUser();

     void deleteByUser();
 }
