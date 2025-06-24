package com.zbkj.admin.controller;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.page.PageDiy;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageDiyEditNameRequest;
import com.zbkj.common.request.page.PageDiyRequest;
import com.zbkj.common.response.page.PageDiyResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PageDiyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * DIY数据表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/pagediy")
@Api(tags = "DIY 控制器") //配合swagger使用

public class PageDiyController {

    @Autowired
    private PageDiyService pageDiyService;

    /**
     * 分页显示DIY数据表
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('admin:pagediy:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "搜索关键子", dataType = "String")
    })
    public CommonResult<CommonPage<PageDiy>> getList(@RequestParam String name,PageParamRequest pageParamRequest){
        CommonPage<PageDiy> pageDiyCommonPage = CommonPage.restPage(pageDiyService.getList(name, pageParamRequest));
        return CommonResult.success(pageDiyCommonPage);
    }

    /**
     * 设置首页
     * @param id 待设置的首页id
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('admin:pagediy:setdefault')")
    @ApiOperation(value = "设置商城首页")
    @RequestMapping(value = "/setdefault/{id}", method = RequestMethod.GET)
    public CommonResult<String> setDefault(@PathVariable(value = "id") Integer id ){
        if(pageDiyService.setDiyPageHome(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 获取已经设置的首页模版数据
     * @return 首页模版id
     */
    @PreAuthorize("hasAuthority('admin:pagediy:getdefault')")
    @ApiOperation(value = "获取商城首页")
    @RequestMapping(value = "/getdefault", method = RequestMethod.GET)
    public CommonResult<Integer> getDefault(){
        return CommonResult.success(pageDiyService.getDiyPageHome(Boolean.TRUE).getId());
    }

    /**
     * 新增DIY数据表
     * @param pageDiyRequest 新增参数
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('admin:pagediy:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<PageDiy> save(@RequestBody @Validated PageDiyRequest pageDiyRequest){
        PageDiy pageDiy = new PageDiy();
        BeanUtils.copyProperties(pageDiyRequest, pageDiy);
        pageDiy.setValue(JSON.toJSONString(pageDiyRequest.getValue()));
        return CommonResult.success(pageDiyService.savePageDiy(pageDiy));
    }

    /**
     * 删除DIY数据表
     * @param id Integer
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('admin:pagediy:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(pageDiyService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改DIY数据表
     * @param pageDiyRequest 修改参数
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('admin:pagediy:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated PageDiyRequest pageDiyRequest){
        PageDiy pageDiy = new PageDiy();
        BeanUtils.copyProperties(pageDiyRequest, pageDiy);
        pageDiy.setValue(JSON.toJSONString(pageDiyRequest.getValue()));
        if(pageDiyService.editPageDiy(pageDiy)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * DIY 模版名称更新
     * @param pageDiyEditNameRequest 更新模版名称对象
     * @return 更新结果
     */
    @PreAuthorize("hasAuthority('admin:pagediy:updatename')")
    @ApiOperation(value = "DIY 模版名称更新")
    @RequestMapping(value = "/updatename", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated PageDiyEditNameRequest pageDiyEditNameRequest){
        if(pageDiyService.editPageDiyName(pageDiyEditNameRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询DIY数据表信息
     * @param id Integer
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('admin:pagediy:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<PageDiyResponse> info(@PathVariable(value = "id") Integer id){
        PageDiy pageDiy = pageDiyService.getDiyPageByPageIdForAdmin(id);
        if(ObjectUtil.isNull(pageDiy)) throw new CrmebException("未找到对应模版信息");
        PageDiyResponse response = new PageDiyResponse();
        BeanUtils.copyProperties(pageDiy, response);
        response.setValue(JSON.parseObject(pageDiy.getValue()));
        return CommonResult.success(response);
   }
}



