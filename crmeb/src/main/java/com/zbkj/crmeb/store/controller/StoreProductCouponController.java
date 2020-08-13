package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreProductCoupon;
import com.zbkj.crmeb.store.request.StoreProductCouponRequest;
import com.zbkj.crmeb.store.request.StoreProductCouponSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


/**
 *  前端控制器
 */
@Slf4j
@RestController
//@RequestMapping("api/admin/store-product-coupon")
@Api(tags = "") //配合swagger使用

public class StoreProductCouponController {

    @Autowired
    private StoreProductCouponService storeProductCouponService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-08-07
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductCoupon>>  getList(@Validated StoreProductCouponSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreProductCoupon> storeProductCouponCommonPage = CommonPage.restPage(storeProductCouponService.getList(request, pageParamRequest));
        return CommonResult.success(storeProductCouponCommonPage);
    }

    /**
     * 新增
     * @param storeProductCouponRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-08-07
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreProductCouponRequest storeProductCouponRequest){
        StoreProductCoupon storeProductCoupon = new StoreProductCoupon();
        BeanUtils.copyProperties(storeProductCouponRequest, storeProductCoupon);

        if(storeProductCouponService.save(storeProductCoupon)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-08-07
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeProductCouponService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改
     * @param id integer id
     * @param storeProductCouponRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-08-07
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreProductCouponRequest storeProductCouponRequest){
        StoreProductCoupon storeProductCoupon = new StoreProductCoupon();
        BeanUtils.copyProperties(storeProductCouponRequest, storeProductCoupon);
        storeProductCoupon.setId(id);

        if(storeProductCouponService.updateById(storeProductCoupon)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-08-07
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductCoupon> info(@RequestParam(value = "id") Integer id){
        StoreProductCoupon storeProductCoupon = storeProductCouponService.getById(id);
        return CommonResult.success(storeProductCoupon);
   }
}



