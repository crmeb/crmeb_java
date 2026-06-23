package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.product.StoreProductGuarantee;
import com.zbkj.common.request.StoreProductGuaranteeRequest;
import com.zbkj.common.response.StoreProductGuaranteeResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.service.dao.StoreProductGuaranteeDao;
import com.zbkj.service.service.StoreProductGuaranteeService;
import com.zbkj.service.service.StoreProductService;
import com.zbkj.service.service.SystemAttachmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreProductGuaranteeServiceImpl extends ServiceImpl<StoreProductGuaranteeDao, StoreProductGuarantee> implements StoreProductGuaranteeService {

    @Resource
    private StoreProductGuaranteeDao dao;
    @Autowired
    private StoreProductService productService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 保障服务列表
     *
     * @param isShow 是否显示
     * @return List
     */
    @Override
    public List<StoreProductGuaranteeResponse> getAdminList(Boolean isShow) {
        LambdaQueryWrapper<StoreProductGuarantee> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreProductGuarantee::getIsDel, false);
        if (isShow) {
            lqw.eq(StoreProductGuarantee::getIsShow, Boolean.TRUE);
        }
        lqw.orderByDesc(StoreProductGuarantee::getSort);
        List<StoreProductGuarantee> guaranteeList = dao.selectList(lqw);
        if (CollUtil.isEmpty(guaranteeList)) {
            return CollUtil.newArrayList();
        }
        // 查询保障服务使用的商户数跟商品数（通过商品查询）
        return guaranteeList.stream().map(guarantee -> {
            StoreProductGuaranteeResponse response = new StoreProductGuaranteeResponse();
            BeanUtils.copyProperties(guarantee, response);
            //response.setProNum(0);
            //List<Product> productList = productService.findUseGuarantee(guarantee.getId());
            //if (CollUtil.isNotEmpty(productList)) {
            //    response.setProNum(productList.size());
            //}
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 新增保障服务
     * @param request 新增参数
     * @return Boolean
     */
    @Override
    public Boolean add(StoreProductGuaranteeRequest request) {
        validateName(request.getName());
        StoreProductGuarantee guarantee = new StoreProductGuarantee();
        BeanUtils.copyProperties(request, guarantee);
        guarantee.setId(null);
        guarantee.setIcon(systemAttachmentService.clearPrefix(guarantee.getIcon()));
        if (ObjectUtil.isNull(request.getIsShow())) {
            guarantee.setIsShow(true);
        }
        return dao.insert(guarantee) > 0;
    }

    /**
     * 删除保障服务
     * @param id 保障服务ID
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        StoreProductGuarantee guarantee = getByIdException(id);
        // 删除前需要判断是否有商品使用，如果有则不能删除
        if (productService.isUseGuarantee(guarantee.getId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_GUARANTEE_USED);
        }
        guarantee.setIsDel(Boolean.TRUE);
        return updateById(guarantee);
    }

    /**
     * 修改保障服务
     * @param request 修改参数
     * @return Boolean
     */
    @Override
    public Boolean edit(StoreProductGuaranteeRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "保障服务id不能为空");
        }
        StoreProductGuarantee oldGuarantee = getByIdException(request.getId());
        if (!oldGuarantee.getName().equals(request.getName())) {
            validateName(request.getName());
        }
        StoreProductGuarantee guarantee = new StoreProductGuarantee();
        BeanUtils.copyProperties(request, guarantee);
        guarantee.setIcon(systemAttachmentService.clearPrefix(request.getIcon()));
        guarantee.setUpdateTime(DateUtil.date());
        return updateById(guarantee);
    }

    /**
     * 修改保障服务显示状态
     * @param id 保障服务ID
     * @return Boolean
     */
    @Override
    public Boolean updateShowStatus(Integer id) {
        StoreProductGuarantee guarantee = getByIdException(id);
        guarantee.setIsShow(!guarantee.getIsShow());
        return updateById(guarantee);
    }

    /**
     * 保障服务列表
     * @return List
     */
    @Override
    public List<StoreProductGuarantee> findByIdList(List<Integer> idList) {
        LambdaQueryWrapper<StoreProductGuarantee> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreProductGuarantee::getIsDel, false);
        lqw.eq(StoreProductGuarantee::getIsShow, true);
        lqw.in(StoreProductGuarantee::getId, idList);
        lqw.orderByDesc(StoreProductGuarantee::getSort);
        return dao.selectList(lqw);
    }

    private StoreProductGuarantee getByIdException(Integer id) {
        StoreProductGuarantee guarantee = getById(id);
        if (ObjectUtil.isNull(guarantee) || guarantee.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_GUARANTEE_NOT_EXIST);
        }
        return guarantee;
    }

    /**
     * 检测名称是否重复
     * @param name 名称
     */
    private void validateName(String name) {
        LambdaQueryWrapper<StoreProductGuarantee> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProductGuarantee::getId);
        lqw.eq(StoreProductGuarantee::getName, name);
        lqw.eq(StoreProductGuarantee::getIsDel, false);
        lqw.last("limit 1");
        StoreProductGuarantee productGuarantee = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(productGuarantee)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "保障服务名称已存在");
        }
    }

}
