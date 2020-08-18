package com.zbkj.crmeb.front.controller;


import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.zbkj.crmeb.express.model.Express;
import com.zbkj.crmeb.express.request.ExpressSearchRequest;
import com.zbkj.crmeb.express.service.ExpressService;
import com.zbkj.crmeb.system.service.SystemCityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户表 前端控制器
 */
@Slf4j
@RestController("ExpressFrontController")
@RequestMapping("api/front")
@Api(tags = "物流公司")
public class ExpressController {
    @Autowired
    private ExpressService expressService;

    /**
     * 物流公司列表
     * @author Mr.Zhang
     * @since 2020-06-01
     */
    @ApiOperation(value = "列表")
    @RequestMapping(value = "/logistics", method = RequestMethod.GET)
    public CommonResult<CommonPage<Express>> register(){
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(Constants.EXPORT_MAX_LIMIT);

        ExpressSearchRequest expressSearchRequest = new ExpressSearchRequest();
        expressSearchRequest.setIsShow(1);
        return CommonResult.success(CommonPage.restPage(expressService.getList(expressSearchRequest, pageParamRequest)));
    }
}



