package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.request.ProductRankingRequest;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.record.ProductDayRecord;
import com.zbkj.service.dao.ProductDayRecordDao;
import com.zbkj.service.service.ProductDayRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ShoppingProductDayRecordService 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class ProductDayRecordServiceImpl extends ServiceImpl<ProductDayRecordDao, ProductDayRecord> implements ProductDayRecordService {

    @Resource
    private ProductDayRecordDao dao;

    /**
     * 获取商品排行榜
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductDayRecord> getRanking(ProductRankingRequest request) {
        String startDate;
        String endDate;
        DateTime date = DateUtil.date();
        switch (request.getDateLimit()){
            case Constants.SEARCH_DATE_YESTERDAY:
                startDate = DateUtil.yesterday().toString("yyyy-MM-dd");
                endDate = DateUtil.yesterday().toString("yyyy-MM-dd");
                break;
            case Constants.SEARCH_DATE_LATELY_7:
                startDate = DateUtil.offsetDay(date, -7).toString("yyyy-MM-dd");
                endDate = DateUtil.yesterday().toString("yyyy-MM-dd");
                break;
            case Constants.SEARCH_DATE_LATELY_30:
                startDate = DateUtil.offsetDay(date, -30).toString("yyyy-MM-dd");
                endDate = DateUtil.yesterday().toString("yyyy-MM-dd");
                break;
            case Constants.SEARCH_DATE_WEEK:
                startDate = DateUtil.beginOfWeek(date).toString("yyyy-MM-dd");
                endDate = DateUtil.endOfWeek(date).toString("yyyy-MM-dd");
                break;
            case Constants.SEARCH_DATE_MONTH:
                startDate = DateUtil.beginOfMonth(date).toString("yyyy-MM-dd");
                endDate = DateUtil.endOfMonth(date).toString("yyyy-MM-dd");
                break;
            case Constants.SEARCH_DATE_YEAR:
                startDate = DateUtil.beginOfYear(date).toString("yyyy-MM-dd");
                endDate = DateUtil.endOfYear(date).toString("yyyy-MM-dd");
                break;
            default:
                String[] split = request.getDateLimit().split(",");
                if (split.length < 2) {
                    throw new CrmebException("请选择正确的时间范围");
                }
                startDate = DateUtil.parse(split[0]).toString("yyyy-MM-dd");
                endDate = DateUtil.parse(split[1]).toString("yyyy-MM-dd");
                break;
        }
        Page<Object> startPage = PageHelper.startPage(request.getPage(), request.getLimit());

        QueryWrapper<ProductDayRecord> wrapper = new QueryWrapper<>();
        wrapper.select("product_id", "sum(page_view) as page_view", "sum(collect_num) as collect_num",
                "sum(add_cart_num) as add_cart_num",
                "sum(order_product_num) as order_product_num",
                "sum(order_success_product_fee) as order_success_product_fee");
        wrapper.between("date", startDate, endDate);
        wrapper.groupBy("product_id");
        switch (request.getSortKey()) {
            case "pageviews":
                wrapper.orderByDesc("page_view");
                break;
            case "collectNum":
                wrapper.orderByDesc("collect_num");
                break;
            case "addCartNum":
                wrapper.orderByDesc("add_cart_num");
                break;
            case "salesNum":
                wrapper.orderByDesc("order_product_num");
                break;
            case "salesAmount":
                wrapper.orderByDesc("order_success_product_fee");
                break;
        }
        List<ProductDayRecord> recordList = dao.selectList(wrapper);
        return CommonPage.copyPageInfo(startPage, recordList);
    }
}

