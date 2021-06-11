package com.zbkj.crmeb.store.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.zbkj.crmeb.store.model.StoreProductReply;
import com.zbkj.crmeb.store.request.StoreProductReplyAddRequest;
import com.zbkj.crmeb.store.request.StoreProductReplyCommentRequest;
import com.zbkj.crmeb.store.request.StoreProductReplyRequest;
import com.zbkj.crmeb.store.request.StoreProductReplySearchRequest;
import com.zbkj.crmeb.store.response.StoreProductReplyResponse;
import com.zbkj.crmeb.store.service.StoreProductReplyService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 评论表 前端控制器
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
@Slf4j
@RestController
@RequestMapping("api/admin/store/product/reply")
@Api(tags = "商品 -- 评论") //配合swagger使用
public class StoreProductReplyController {

    @Autowired
    private StoreProductReplyService storeProductReplyService;

    /**
     * 分页显示评论表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductReplyResponse>>  getList(
            @Validated StoreProductReplySearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreProductReplyResponse> storeProductReplyCommonPage =
                CommonPage.restPage(storeProductReplyService.getList(request, pageParamRequest));
        return CommonResult.success(storeProductReplyCommonPage);
    }

    /**
     * 新增评论表
     * @param request 新增参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(
            @RequestBody @Validated StoreProductReplyAddRequest request){
        if(storeProductReplyService.virtualCreate(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除评论表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable Integer id){
        LambdaUpdateWrapper<StoreProductReply> lmdUp = new LambdaUpdateWrapper<>();
        lmdUp.set(StoreProductReply::getIsDel, 1);
        lmdUp.eq(StoreProductReply::getId, id);
        if(storeProductReplyService.update(lmdUp)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改评论表
     * @param storeProductReplyRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(
            @RequestBody @Validated StoreProductReplyRequest storeProductReplyRequest){
        StoreProductReply storeProductReply = new StoreProductReply();
        BeanUtils.copyProperties(storeProductReplyRequest, storeProductReply);
        storeProductReply.setId(storeProductReplyRequest.getId());
        if(storeProductReplyService.updateById(storeProductReply)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询评论表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<StoreProductReply> info(@PathVariable Integer id){
        StoreProductReply storeProductReply = storeProductReplyService.getById(id);
        return CommonResult.success(storeProductReply);
   }

    /**
     * 回复商品评论
     * @param request  StoreProductReplyCommentRequest 回复参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
   @ApiOperation(value = "回复")
   @RequestMapping(value = "/comment", method = RequestMethod.POST)
   public CommonResult<String> comment(@RequestBody StoreProductReplyCommentRequest request){
        List<Integer> ids = CrmebUtil.stringToArray(request.getIds());
       for (Integer id : ids) {
           LambdaUpdateWrapper<StoreProductReply> lup = new LambdaUpdateWrapper<>();
           lup.eq(StoreProductReply::getId, id);
           lup.set(StoreProductReply::getMerchantReplyContent, request.getMerchantReplyContent());
           lup.set(StoreProductReply::getMerchantReplyTime, DateUtil.getNowTime());
           lup.set(StoreProductReply::getIsReply, true);
           storeProductReplyService.update(lup);
       }
        return CommonResult.success();
   }
}



