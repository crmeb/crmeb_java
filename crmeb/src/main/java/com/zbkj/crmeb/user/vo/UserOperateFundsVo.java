package com.zbkj.crmeb.user.vo;

import io.swagger.annotations.ApiModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 资金操作
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
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="资金操作", description="资金操作")
public class UserOperateFundsVo implements Serializable {

    private static final long serialVersionUID=1L;
    public UserOperateFundsVo(){}
    public UserOperateFundsVo(Integer uid, String foundsType, String value) {
        this.uid = uid;
        this.foundsType = foundsType;
        this.value = value;
    }

    private Integer uid;

    private String foundsType;

    private String value;
}
