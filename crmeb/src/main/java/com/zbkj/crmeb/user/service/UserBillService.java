package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.request.FundsMonitorRequest;
import com.zbkj.crmeb.finance.request.FundsMonitorSearchRequest;
import com.zbkj.crmeb.front.response.UserSpreadCommissionResponse;
import com.zbkj.crmeb.store.request.StoreOrderRefundRequest;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.response.BillType;
import com.zbkj.crmeb.user.response.UserBillResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * UserBillService 接口实现
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
public interface UserBillService extends IService<UserBill> {

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<UserBill>
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    List<UserBill> getList(FundsMonitorSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增/消耗 总数
     *
     * @param pm       Integer 0 = 支出 1 = 获得
     * @param userId   Integer 用户uid
     * @param category String 类型
     * @param date     String 时间范围
     * @param type     String 小类型
     * @return UserBill
     * @author Mr.Zhang
     * @since 2020-05-29
     */
    Integer getSumInteger(Integer pm, Integer userId, String category, String date, String type);

    /**
     * 新增/消耗  总金额
     *
     * @param pm       Integer 0 = 支出 1 = 获得
     * @param userId   Integer 用户uid
     * @param category String 类型
     * @param date     String 时间范围
     * @param type     String 小类型
     * @return UserBill
     * @author Mr.Zhang
     * @since 2020-05-29
     */
    BigDecimal getSumBigDecimal(Integer pm, Integer userId, String category, String date, String type);

    /**
     * 按照月份分组, 余额
     *
     * @return CommonPage<UserBill>
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    PageInfo<UserSpreadCommissionResponse> getListGroupByMonth(Integer userId, List<String> typeList, PageParamRequest pageParamRequest, String category);

    /**
     * 保存退款日志
     *
     * @return boolean
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    boolean saveRefundBill(StoreOrderRefundRequest request, User user);

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<UserBill>
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    PageInfo<UserBillResponse> getListAdmin(FundsMonitorSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取资金操作类型
     *
     * @return 操作类型集合，从数据库group by(type)查询获取
     */
    List<UserBill> getBillGroupType();

    /**
     * 返回资金操作类型 仅仅转换数据用
     *
     * @return 操作类型
     */
    List<BillType> getBillType();

    /**
     * 根据基本条件查询
     *
     * @param bill 基本参数
     * @return 查询结果
     */
    List<UserBill> getByEntity(UserBill bill);

    /**
     * 查询搜索明细类型参数
     *
     * @return 明细类型集合
     */
    List<UserBill> getSearchOption();

    /**
     * 获取订单历史处理记录(退款使用)
     *
     * @param orderId 订单id
     * @param uid     用户id
     */
    List<UserBill> findListByOrderIdAndUid(Integer orderId, Integer uid);

    /**
     * 资金监控
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserBillResponse> fundMonitoring(FundsMonitorRequest request, PageParamRequest pageParamRequest);

    /**
     * 用户账单记录（现金）
     *
     * @param uid  用户uid
     * @param type 记录类型：all-全部，expenditure-支出，income-收入
     * @return
     */
    PageInfo<UserBill> nowMoneyBillRecord(Integer uid, String type, PageParamRequest pageRequest);

    /**
     * 获取H5列表
     *
     * @param userId   Integer 用户uid
     * @param category String 类型
     * @param pageParamRequest 分页类型
     * @return List<UserBill>
     */
    List<UserBill> getH5List(Integer userId, String category, PageParamRequest pageParamRequest);
}