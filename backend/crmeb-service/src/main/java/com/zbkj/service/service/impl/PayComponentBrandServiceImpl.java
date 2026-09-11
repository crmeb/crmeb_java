package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.vo.ShopBrandVo;
import com.zbkj.common.model.wechat.video.PayComponentBrand;
import com.zbkj.service.dao.PayComponentBrandDao;
import com.zbkj.service.service.PayComponentBrandService;
import com.zbkj.service.service.WechatVideoSpuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class PayComponentBrandServiceImpl extends ServiceImpl<PayComponentBrandDao, PayComponentBrand> implements PayComponentBrandService {

    private final Logger logger = LoggerFactory.getLogger(PayComponentBrandServiceImpl.class);

    @Resource
    private PayComponentBrandDao dao;

    @Autowired
    private WechatVideoSpuService wechatVideoSpuService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 更新数据
     */
    @Override
    public void updateData() {
        List<ShopBrandVo> shopBrandList = wechatVideoSpuService.getShopBrandList();
        if (CollUtil.isEmpty(shopBrandList)) {
            logger.info("微信未返回品牌信息");
            return ;
        }
        List<PayComponentBrand> brandList = shopBrandList.stream().map(e -> {
            PayComponentBrand brand = new PayComponentBrand();
            BeanUtils.copyProperties(e, brand);
            return brand;
        }).collect(Collectors.toList());

        Boolean execute = transactionTemplate.execute(e -> {
            saveBatch(brandList);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("更新自定义交易品牌列表,操作数据库时出错");
        }
        logger.info(StrUtil.format("自动更新自定义交易品牌列表成功，时间{}", DateUtil.now()));
    }
}

