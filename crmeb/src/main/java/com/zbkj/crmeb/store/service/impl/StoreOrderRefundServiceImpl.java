package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.Constants;
import com.exception.CrmebException;
import com.zbkj.crmeb.store.dao.StoreOrderDao;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.StoreOrderRefundRequest;
import com.zbkj.crmeb.store.service.StoreOrderRefundService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
* @author Mr.Zhang
* @Description StoreOrderServiceImpl 接口实现
* @since 2020-05-28
*/
@Service
public class StoreOrderRefundServiceImpl extends ServiceImpl<StoreOrderDao, StoreOrder> implements StoreOrderRefundService {


    /**
     * 退款 需要优化
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @Async
    @Override
    public void refund(StoreOrderRefundRequest request, StoreOrder storeOrder) {
        try {
            //开始处理
            if(storeOrder.getIsChannel() == Constants.ORDER_PAY_CHANNEL_PUBLIC){
                //公众号
            }else{
                //小程序
                
            }

        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }
    }
}

