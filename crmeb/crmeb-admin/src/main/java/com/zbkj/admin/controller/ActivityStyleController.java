package com.zbkj.admin.controller;

import cn.hutool.core.date.DateUtil;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.model.acticitystyle.ActivityStyle;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ActivityStyleRequest;
import com.zbkj.common.request.ActivityStyleSearchRequest;
import com.zbkj.common.request.ActivityStyleUpdateStatusRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.ActivityStyleResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.ActivityStyleService;
import com.zbkj.service.service.SystemAttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * 活动样式 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/activitystyle")
@Api(tags = "活动样式") //配合swagger使用
public class ActivityStyleController {

    @Autowired
    private ActivityStyleService activityStyleService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 分页显示
     *
     * @param request          搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2023-01-05
     */
    @PreAuthorize("hasAuthority('admin:activitystyle:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ActivityStyleResponse>> getList(@Validated ActivityStyleSearchRequest request,
                                                                   @ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<ActivityStyleResponse> activityStyleCommonPage = CommonPage.restPage(activityStyleService.getList(request, pageParamRequest));
        return CommonResult.success(activityStyleCommonPage);
    }

    /**
     * 新增
     *
     * @param activityStyleRequest 新增参数
     * @author dazongzi
     * @since 2023-01-05
     */
    @PreAuthorize("hasAuthority('admin:activitystyle:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ActivityStyleRequest activityStyleRequest) {
        ActivityStyle activityStyle = new ActivityStyle();
        BeanUtils.copyProperties(activityStyleRequest, activityStyle);
        activityStyle.setStarttime(CrmebDateUtil.strToDate(activityStyleRequest.getStarttime(), DateConstants.DATE_FORMAT));
        activityStyle.setEndtime(CrmebDateUtil.strToDate(activityStyleRequest.getEndtime(), DateConstants.DATE_FORMAT));
        activityStyle.setStyle(systemAttachmentService.clearPrefix(activityStyle.getStyle()));
        if (activityStyleService.save(activityStyle)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除
     *
     * @param id Integer
     * @author dazongzi
     * @since 2023-01-05
     */
    @PreAuthorize("hasAuthority('admin:activitystyle:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (activityStyleService.removeById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改
     *
     * @param activityStyleRequest 修改参数
     * @author dazongzi
     * @since 2023-01-05
     */
    @PreAuthorize("hasAuthority('admin:activitystyle:edite')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ActivityStyleRequest activityStyleRequest) {
        ActivityStyle activityStyle = new ActivityStyle();
        BeanUtils.copyProperties(activityStyleRequest, activityStyle);
        activityStyle.setId(activityStyleRequest.getId());
        activityStyle.setStyle(systemAttachmentService.clearPrefix(activityStyle.getStyle()));
        activityStyle.setStarttime(CrmebDateUtil.strToDate(activityStyleRequest.getStarttime(), DateConstants.DATE_FORMAT));
        activityStyle.setEndtime(CrmebDateUtil.strToDate(activityStyleRequest.getEndtime(), DateConstants.DATE_FORMAT));
        activityStyle.setUpdatetime(DateUtil.date());
        if (activityStyleService.updateById(activityStyle)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 更新状态
     */
    @PreAuthorize("hasAuthority('admin:activitystyle:updatestatus')")
    @ApiOperation(value = "更新状态")
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@RequestBody @Validated ActivityStyleUpdateStatusRequest activityStyleUpdateStatusRequest) {
        boolean result = activityStyleService.updateStatus(activityStyleUpdateStatusRequest.getId(), activityStyleUpdateStatusRequest.getStatus());
        if (result) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }
}



