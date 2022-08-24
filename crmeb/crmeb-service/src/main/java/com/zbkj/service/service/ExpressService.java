package com.zbkj.service.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.request.ExpressSearchRequest;
import com.zbkj.common.request.ExpressUpdateRequest;
import com.zbkj.common.request.ExpressUpdateShowRequest;

import java.util.List;

/**
*  ExpressService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface ExpressService extends IService<Express> {

    /**
    * 列表
    * @param request 搜索条件
    * @param pageParamRequest 分页类参数
    * @return List<Express>
    */
    List<Express> getList(ExpressSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 编辑
     */
    Boolean updateExpress(ExpressUpdateRequest expressRequest);

    /**
     * 修改显示状态
     */
    Boolean updateExpressShow(ExpressUpdateShowRequest expressRequest);

    /**
     * 同步快递公司
     */
    Boolean syncExpress();

    /**
     * 查询全部快递公司
     * @param type 类型：normal-普通，elec-电子面单
     */
    List<Express> findAll(String type);

    /**
     * 查询快递公司面单模板
     * @param com 快递公司编号
     */
    JSONObject template(String com);

    /**
     * 查询快递公司
     * @param code 快递公司编号
     * @return Express
     */
    Express getByCode(String code);

    /**
     * 通过物流公司名称获取
     * @param name 物流公司名称
     */
    Express getByName(String name);

    /**
     * 获取快递公司详情
     * @param id 快递公司id
     */
    Express getInfo(Integer id);
}
