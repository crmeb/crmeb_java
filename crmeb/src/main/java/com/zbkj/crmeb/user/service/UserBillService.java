package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.request.FundsMonitorSearchRequest;
import com.zbkj.crmeb.front.response.UserSpreadCommissionResponse;
import com.zbkj.crmeb.store.request.StoreOrderRefundRequest;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.request.UserBillDetailListRequest;
import com.zbkj.crmeb.user.response.BillType;
import com.zbkj.crmeb.user.response.UserBillResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return List<UserBill>
     */
    List<UserBill> getList(FundsMonitorSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增/消耗 总数
     * @param pm Integer 0 = 支出 1 = 获得
     * @param userId Integer 用户uid
     * @param category String 类型
     * @param date String 时间范围
     * @param type String 小类型
     * @author Mr.Zhang
     * @since 2020-05-29
     * @return UserBill
     */
    Integer getSumInteger(Integer pm, Integer userId, String category, String date, String type);

    /**
     * 新增/消耗  总金额
     * @param pm Integer 0 = 支出 1 = 获得
     * @param userId Integer 用户uid
     * @param category String 类型
     * @param date String 时间范围
     * @param type String 小类型
     * @author Mr.Zhang
     * @since 2020-05-29
     * @return UserBill
     */
    BigDecimal getSumBigDecimal(Integer pm, Integer userId, String category, String date, String type);

    /**
     * 按照月份分组, 余额
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return CommonPage<UserBill>
     */
    PageInfo<UserSpreadCommissionResponse> getListGroupByMonth(Integer userId, List<String> typeList, PageParamRequest pageParamRequest, String category);

    /**
     * 保存退款日志
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return boolean
     */
    boolean saveRefundBill(StoreOrderRefundRequest request, User user);

    /**
     * 反还佣金日志
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    void saveRefundBrokeragePriceBill(StoreOrderRefundRequest request, User user);

    /**
     * 反还积分日志
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    void saveRefundIntegralBill(StoreOrderRefundRequest request, User user);

    /**
     * 根据用户id获取对应的佣金数据 分销using
     * @param userId 用户id
     * @return 佣金数据
     */
    BigDecimal getDataByUserId(Integer userId);

    /**
     * 通过订单获取
     * @param id 订单id
     * @param userId 用户id
     * @param pm 类型
     * @return
     */
    BigDecimal getIntegerByOrder(Integer id, Integer userId, int pm);

    /**
     * 列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return List<UserBill>
     */
    PageInfo<UserBillResponse> getListAdmin(FundsMonitorSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 列表
     * @param request 请求参数
     * @param monthList List<String> 分页类参数
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return List<UserBill>
     */
    Map<String, Integer> getCountListByMonth(FundsMonitorSearchRequest request, List<String> monthList);

    /**
     * 佣金排行榜
     * @param type  String 时间范围
     * @param pageParamRequest PageParamRequest 分页
     * @author Mr.Zhang
     * @since 2020-05-25
     * @return List<LoginResponse>
     */
    List<UserBill> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest);

    /**
     * 获取资金操作类型
     * @return 操作类型集合，从数据库group by(type)查询获取
     */
    List<UserBill> getBillGroupType();

    /**
     * 返回资金操作类型 仅仅转换数据用
     * @return 操作类型
     */
    List<BillType> getBillType();

    /**
     * 获取佣金总额
     * @return 佣金总额
     */
    BigDecimal getSumBrokerage();

    /**
     * 根据基本条件查询
     * @param bill  基本参数
     * @return  查询结果
     */
    List<UserBill> getByEntity(UserBill bill);

    /**
     * Base serch
     * @param userId 用户Id
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return 查询结果
     */
    PageInfo<UserBillResponse> getByBaseSearch(Integer userId, UserBillDetailListRequest request, PageParamRequest pageParamRequest);

    /**
     * 查询搜索明细类型参数
     * @return  明细类型集合
     */
    List<UserBill> getSearchOption();
}