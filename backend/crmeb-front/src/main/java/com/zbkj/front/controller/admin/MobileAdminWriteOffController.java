package com.zbkj.front.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.response.StoreOrderVerificationConfirmResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.MobileAdminWriteOffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 移动端商家订单核销
 */
@RestController
@RequestMapping("api/front")
@Api(tags = "移动端商家管理-核销")
public class MobileAdminWriteOffController {

    @Autowired
    private MobileAdminWriteOffService mobileAdminWriteOffService;

    @ApiOperation(value = "核销码查询待核销订单")
    @RequestMapping(value = "/admin/order/writeConfirm/{vCode}", method = RequestMethod.GET)
    public CommonResult<StoreOrderVerificationConfirmResponse> writeConfirm(@PathVariable String vCode) {
        return CommonResult.success(mobileAdminWriteOffService.confirm(vCode));
    }

    @ApiOperation(value = "核销码核销订单")
    @RequestMapping(value = "/admin/order/writeUpdate/{vCode}", method = RequestMethod.GET)
    public CommonResult<Boolean> writeUpdate(@PathVariable String vCode) {
        return CommonResult.success(mobileAdminWriteOffService.writeOff(vCode));
    }

    @ApiOperation(value = "兼容参考移动端核销接口")
    @RequestMapping(value = "/order/order_verific", method = RequestMethod.POST)
    public CommonResult<Object> orderVerific(@RequestBody Map<String, Object> params) {
        String verifyCode = getString(params, "verify_code");
        if (StrUtil.isBlank(verifyCode)) {
            verifyCode = getString(params, "verifyCode");
        }
        if (StrUtil.isBlank(verifyCode)) {
            throw new CrmebException("核销码不能为空");
        }
        Integer isConfirm = getInteger(params, "is_confirm", 0);
        if (isConfirm != null && isConfirm == 1) {
            return CommonResult.success(mobileAdminWriteOffService.writeOff(verifyCode));
        }
        return CommonResult.success(mobileAdminWriteOffService.confirm(verifyCode));
    }

    private String getString(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private Integer getInteger(Map<String, Object> params, String key, Integer defaultValue) {
        Object value = params.get(key);
        if (value == null || StrUtil.isBlank(String.valueOf(value))) {
            return defaultValue;
        }
        return Integer.valueOf(String.valueOf(value));
    }
}
