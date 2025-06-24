package com.zbkj.front.controller;


import cn.hutool.core.util.ObjectUtil;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.response.page.PageDiyResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PageDiyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * DIY数据表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/front/pagediy")
@Api(tags = "DIY 控制器") //配合swagger使用

public class PageDiyController {

    @Autowired
    private PageDiyService pageDiyService;
    /**
     * 查询DIY数据表信息
     * @param id Integer
     * @author dazongzi
     * @since 2023-05-16
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<PageDiyResponse> info(@PathVariable(value = "id") Integer id){
        PageDiyResponse response = pageDiyService.getDiyPageByPageIdForFront(id);
        if(ObjectUtil.isNull(response)) throw new CrmebException("未找到对应模版信息");
        return CommonResult.success(response);
   }
}



