package com.zbkj.crmeb.export.controller;

import cn.hutool.core.collection.CollUtil;
import com.common.CommonResult;
import com.utils.ExcelUtil;
import com.zbkj.crmeb.bargain.request.StoreBargainSearchRequest;
import com.zbkj.crmeb.combination.request.StoreCombinationSearchRequest;
import com.zbkj.crmeb.export.service.ExcelService;
import com.zbkj.crmeb.export.vo.ProductExcelVo;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;


/**
 * Excel导出 前端控制器
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
@Slf4j
@RestController
@RequestMapping("api/admin/export/excel")
@Api(tags = "导出 -- Excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    /**
     * 商品导出
     * @param request 搜索条件
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @ApiOperation(value = "产品")
    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> export(@Validated StoreProductSearchRequest request, HttpServletResponse response){
//        List<ProductExcelVo> productExcelVoList = excelService.product(request, response);
//        ExcelUtil.setSheetName("store");   //sheet名称
//        ExcelUtil.setFileName("产品导出");  //文件名称前缀  xx_yyyymmddhhiiss
//        ExcelUtil.writeExcel(response, productExcelVoList, ProductExcelVo.class);
        String fileName = excelService.exportProduct(request, response);
        HashMap<String, String> map = CollUtil.newHashMap();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

    /**
     * 砍价商品导出
     * @param request 搜索条件
     * @author HZW
     * @since 2020-05-06
     * @return
     */
    @ApiOperation(value = "砍价商品导出")
    @RequestMapping(value = "/bargain/product", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> exportBargainProduct(@Validated StoreBargainSearchRequest request, HttpServletResponse response){
        String fileName = excelService.exportBargainProduct(request, response);
        HashMap<String, String> map = CollUtil.newHashMap();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

    /**
     * 拼团商品导出
     * @param request 搜索条件
     * @author HZW
     * @since 2020-05-06
     * @return
     */
    @ApiOperation(value = "拼团商品导出")
    @RequestMapping(value = "/combiantion/product", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> exportCombinationProduct(@Validated StoreCombinationSearchRequest request, HttpServletResponse response){
        String fileName = excelService.exportCombinationProduct(request, response);
        HashMap<String, String> map = CollUtil.newHashMap();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

}



