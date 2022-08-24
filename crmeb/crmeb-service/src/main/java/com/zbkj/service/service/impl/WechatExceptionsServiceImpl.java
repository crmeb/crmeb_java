package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.model.wechat.WechatExceptions;
import com.zbkj.service.dao.WechatExceptionsDao;
import com.zbkj.service.service.WechatExceptionsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 *  微信异常服务实现类
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
@Service
public class WechatExceptionsServiceImpl extends ServiceImpl<WechatExceptionsDao, WechatExceptions> implements WechatExceptionsService {

    @Resource
    private WechatExceptionsDao dao;

    /**
     * 删除历史日志
     */
    @Override
    public void autoDeleteLog() {
        String beforeDate = DateUtil.offsetDay(new Date(), -9).toString(Constants.DATE_FORMAT_DATE);
        UpdateWrapper<WechatExceptions> wrapper = Wrappers.update();
        wrapper.lt("create_time", beforeDate);
        dao.delete(wrapper);
    }
}

