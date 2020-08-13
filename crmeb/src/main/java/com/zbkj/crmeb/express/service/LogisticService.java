package com.zbkj.crmeb.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.express.model.Express;
import com.zbkj.crmeb.express.request.ExpressSearchRequest;
import com.zbkj.crmeb.express.vo.LogisticsResultVo;

import java.util.List;

/**
* @author Mr.Zhang
* @Description ExpressService 接口
* @since 2020-04-17
*/
public interface LogisticService {
    LogisticsResultVo info(String expressNo, String type);
}