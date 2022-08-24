package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.request.*;
import com.zbkj.common.response.StoreProductInfoResponse;
import com.zbkj.common.response.StoreProductResponse;
import com.zbkj.common.response.StoreProductTabsHeader;
import com.zbkj.common.vo.MyRecord;
import com.github.pagehelper.PageInfo;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * StoreProductService 接口
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
public interface StoreProductService extends IService<StoreProduct> {

    /**
     * 获取产品列表Admin
     * @param request 筛选参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<StoreProductResponse> getAdminList(StoreProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据id集合获取商品信息
     * @param productIds id集合
     * @return 商品信息
     */
    List<StoreProduct> getListInIds(List<Integer> productIds);

    /**
     * 新增商品
     * @param request 商品请求对象
     * @return Boolean
     */
    Boolean save(StoreProductAddRequest request);

    /**
     * 更新商品信息
     * @param storeProductRequest 商品参数
     * @return 更新结果
     */
    Boolean update(StoreProductAddRequest storeProductRequest);

    /**
     * 产品详情
     * @param id 商品id
     * @return StoreProductResponse
     */
    StoreProductResponse getByProductId(Integer id);

    /**
     * 商品详情（管理端）
     * @param id 商品id
     * @return StoreProductInfoResponse
     */
    StoreProductInfoResponse getInfo(Integer id);

    /**
     * 获取tabsHeader对应数量
     * @return List
     */
    List<StoreProductTabsHeader> getTabsHeader();

    /**
     * 根据其他平台url导入产品信息
     * @param url 待倒入平台的url
     * @param tag 待导入平台标识
     * @return 待导入的商品信息
     */
    StoreProductRequest importProductFromUrl(String url, int tag) throws IOException, JSONException;

    List<Integer> getSecondaryCategoryByProductId(String productId);

    /**
     * 删除商品
     * @param productId 商品id
     * @param type 类型：recycle——回收站 delete——彻底删除
     * @return 删除结果
     */
    Boolean deleteProduct(Integer productId, String type);

    /**
     * 恢复已删除商品
     * @param productId 商品id
     * @return 恢复结果
     */
    Boolean reStoreProduct(Integer productId);

    /**
     * 后台任务批量操作库存
     */
    void consumeProductStock();

    /**
     * 扣减库存任务操作
     * @param storeProductStockRequest 扣减库存参数
     * @return 执行结果
     */
    boolean doProductStock(StoreProductStockRequest storeProductStockRequest);

    /**
     * 获取复制商品配置
     */
    MyRecord copyConfig();

    /**
     * 复制平台商品
     * @param url 商品链接
     * @return
     */
    MyRecord copyProduct(String url);

    /**
     * 添加/扣减库存
     * @param id 商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationStock(Integer id, Integer num, String type);

    /**
     * 下架
     * @param id 商品id
     */
    Boolean offShelf(Integer id);

    /**
     * 上架
     * @param id 商品id
     * @return Boolean
     */
    Boolean putOnShelf(Integer id);

    /**
     * 首页商品列表
     * @param type 类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return CommonPage
     */
    List<StoreProduct> getIndexProduct(Integer type, PageParamRequest pageParamRequest);

    /**
     * 获取商品移动端列表
     * @param request 筛选参数
     * @param pageRequest 分页参数
     * @return List
     */
    List<StoreProduct> findH5List(ProductRequest request, PageParamRequest pageRequest);

    /**
     * 获取移动端商品详情
     * @param id 商品id
     * @return StoreProduct
     */
    StoreProduct getH5Detail(Integer id);

    /**
     * 获取购物车商品信息
     * @param productId 商品编号
     * @return StoreProduct
     */
    StoreProduct getCartByProId(Integer productId);

    /**
     * 根据日期获取新增商品数量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getNewProductByDate(String date);

    /**
     * 获取所有未删除的商品
     * @return List<StoreProduct>
     */
    List<StoreProduct> findAllProductByNotDelte();

    /**
     * 模糊搜索商品名称
     * @param productName 商品名称
     * @return List
     */
    List<StoreProduct> likeProductName(String productName);

    /**
     * 警戒库存数量
     * @return Integer
     */
    Integer getVigilanceInventoryNum();

    /**
     * 销售中（上架）商品数量
     * @return Integer
     */
    Integer getOnSaleNum();

    /**
     * 未销售（仓库）商品数量
     * @return Integer
     */
    Integer getNotSaleNum();

    /**
     * 获取商品排行榜
     * @return List
     */
    List<StoreProduct> getLeaderboard();
}
