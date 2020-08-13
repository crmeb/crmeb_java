package com.zbkj.crmeb.express.service;

import com.alibaba.fastjson.JSONObject;
import com.common.PageParamRequest;
import com.zbkj.crmeb.express.model.Express;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.express.request.ExpressRequest;
import com.zbkj.crmeb.express.request.ExpressSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description ExpressService 接口
* @since 2020-04-17
*/
public interface ExpressService extends IService<Express> {

    /**
    * 列表
    * @param request 搜索条件
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-17
    * @return List<Express>
    */
    List<Express> getList(ExpressSearchRequest request, PageParamRequest pageParamRequest);

    Express info(Integer id);

//    /**
//     * 根据快递单号和快递公司类型查看物流信息
//     * @param no 单号
//     * @param type 快递公司类型
//     */
//    JSONObject getExpressInfo(String no, String type);
}
