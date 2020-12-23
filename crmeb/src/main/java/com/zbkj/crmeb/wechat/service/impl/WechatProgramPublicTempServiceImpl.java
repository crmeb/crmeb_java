package com.zbkj.crmeb.wechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.utils.DateUtil;
import com.zbkj.crmeb.wechat.dao.WechatProgramPublicTempDao;
import com.zbkj.crmeb.wechat.model.WechatProgramPublicTemp;
import com.zbkj.crmeb.wechat.request.WechatProgramPublicTempSearchRequest;
import com.zbkj.crmeb.wechat.service.WeChatService;
import com.zbkj.crmeb.wechat.service.WechatProgramPublicTempService;
import com.zbkj.crmeb.wechat.vo.ProgramTempVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * WechatProgramPublicTempServiceImpl 接口实现
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
@Service
public class WechatProgramPublicTempServiceImpl extends ServiceImpl<WechatProgramPublicTempDao, WechatProgramPublicTemp> implements WechatProgramPublicTempService {

    @Resource
    private WechatProgramPublicTempDao dao;

    @Autowired
    private WeChatService weChatService;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-08-27
    * @return List<WechatProgramPublicTemp>
    */
    @Override
    public List<WechatProgramPublicTemp> getList(WechatProgramPublicTempSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 WechatProgramPublicTemp 类的多条件查询
        LambdaQueryWrapper<WechatProgramPublicTemp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        WechatProgramPublicTemp model = new WechatProgramPublicTemp();
        String title = request.getTitle();
        request.setTitle(null);
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);

        //模板标题模糊查询，其余精确查询
        if(StringUtils.isNotBlank(title)){
            lambdaQueryWrapper.like(WechatProgramPublicTemp::getTitle, title);
        }
        lambdaQueryWrapper.orderByDesc(WechatProgramPublicTemp::getUpdateTime);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 把微信公众号模板同步到数据库
     * @author Mr.Zhang
     * @since 2020-08-27
     * @return List<WechatProgramPublicTemp>
     */
    @Override
    public void async() {
        int page = 1;
        while (true){
            List<ProgramTempVo> programPublicTempList = weChatService.getProgramPublicTempList(page);
            if(null == programPublicTempList || programPublicTempList.size() < 1){
                return;
            }
            WechatProgramPublicTemp wechatProgramPublicTempVo = new WechatProgramPublicTemp();

            for (ProgramTempVo vo : programPublicTempList) {
                WechatProgramPublicTemp wechatProgramPublicTemp = getVoByTid(vo.getTid());
                if(null == wechatProgramPublicTemp){
                    BeanUtils.copyProperties(vo, wechatProgramPublicTempVo);
                    save(wechatProgramPublicTempVo);
                    continue;
                }
                BeanUtils.copyProperties(vo, wechatProgramPublicTemp);
                wechatProgramPublicTemp.setUpdateTime(DateUtil.nowDateTime());
                updateById(wechatProgramPublicTemp);
            }
            page++;
        }
    }

    /**
     * 通过模板号查询模板
     * @param tid Integer 微信模板号
     * @author Mr.Zhang
     * @since 2020-08-27
     * @return WechatProgramPublicTemp
     */
    private WechatProgramPublicTemp getVoByTid(Integer tid) {
        LambdaQueryWrapper<WechatProgramPublicTemp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WechatProgramPublicTemp::getTid, tid);
        return dao.selectOne(lambdaQueryWrapper);
    }

}

