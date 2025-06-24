package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.acticitystyle.ActivityStyle;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.request.ActivityStyleSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.ActivityStyleResponse;

import java.util.List;

/**
* @author dazongzi
* @description ActivityStyleService 接口
* @date 2023-01-05
*/
public interface ActivityStyleService extends IService<ActivityStyle> {

    /**
     * 分页查询活动样式
     * @param request 查询条件
     * @param pageParamRequest 分页对行
     * @return 查询结果
     */
    PageInfo<ActivityStyleResponse> getList(ActivityStyleSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 更新
     * @param id id
     * @param status 活动样式 状态
     * @return 更新状态结果
     */
    boolean updateStatus(Integer id, boolean status);

    /**
     * 根据服务器时间查询正在进行和即将开始的活动边框配置 并缓存在redis中
     * 缓存时常5分钟
     * @param type 0边框 1背景
     * @return 正在进行和未开始的活动边框配置数据
     */
    List<ActivityStyle> getListBeforeBeginTime(boolean type);

    /**
     * 填补活动边框 用于商品列表
     * @param productList 待添加商品数据
     * @return
     */
    List<StoreProduct> makeActivityBorderStyle(List<StoreProduct> productList);

    /**
     * 填补活动背景 用于商品详情
     * @param product 待添加活动背景的商品
     * @return 已经添加活动背景的商品
     */
    String makeActivityBackgroundStyle(StoreProduct product);
}
