package com.zbkj.crmeb.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.RestTemplateUtil;
import com.zbkj.crmeb.express.dao.ExpressDao;
import com.zbkj.crmeb.express.model.Express;
import com.zbkj.crmeb.express.request.ExpressSearchRequest;
import com.zbkj.crmeb.express.service.ExpressService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ExpressServiceImpl 接口实现
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class ExpressServiceImpl extends ServiceImpl<ExpressDao, Express> implements ExpressService {

    @Resource
    private ExpressDao dao;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;


    /**
     * 分页显示快递公司表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @Override
    public List<Express> getList(ExpressSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Express> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(request.getIsShow() != null){
            lambdaQueryWrapper.eq(Express::getIsShow, request.getIsShow());
        }

        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and( i-> i.or().like(Express::getCode, request.getKeywords()).
            or().like(Express::getName, request.getKeywords()));
        }

        lambdaQueryWrapper.orderByDesc(Express::getSort);
        return dao.selectList(lambdaQueryWrapper);
    }

    /** 详情
     * @param id Integer 订单id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return Express
     */
    @Override
    public Express info(Integer id) {
        Express info = getById(id);
        if(null == info){
            throw new CrmebException("没有找到快递公司信息");
        }
        return info;
    }

//    /**
//     * 查看物流信息
//     * @param no 单号
//     * @param type 快递公司类型
//     */
//    @Override
//    public JSONObject getExpressInfo(String no, String type) {
//        String key = systemConfigService.getValueByKey("system_express_app_code");
//        if(StringUtils.isEmpty(key)) throw new CrmebException("未配置物流查询");
//        String url = Constants.LOGISTICS_API_URL+"no="+no+"&type="+type;
//        HashMap<String,String> headers = new HashMap<>();
//        headers.put("Authorization", "APPCODE "+key);
//        return restTemplateUtil.getData(url, headers);
//    }
}

