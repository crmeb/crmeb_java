package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 *  商品类型Vo对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class ShopCatVo {

    /** 错误码 */
    @TableField(value = "errcode")
    private Integer errCode;

    /** 错误信息 */
    @TableField(value = "errmsg")
    private Integer errMsg;

    /** 类目列表 */
    @TableField(value = "third_cat_list")
    private List<ShopCatDetailVo> thirdCatList;
}
