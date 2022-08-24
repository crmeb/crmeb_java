package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 *  自定义交易组件商品列表请求Vo
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
public class ShopSpuListRequestVo {

    /** 商品线上状态：0-初始值，5-上架，11-自主下架，13-违规下架/风控系统下架 */
    // 选填，不填时获取所有状态商品
    private Integer status;

    /** 开始创建时间,格式yyyy-MM-dd HH:mm:ss */
    @TableField(value = "start_create_time")
    private String startCreateTime;

    /** 结束创建时间,格式yyyy-MM-dd HH:mm:ss */
    @TableField(value = "end_create_time")
    private String endCreateTime;

    /** 开始更新时间,格式yyyy-MM-dd HH:mm:ss */
    @TableField(value = "start_update_time")
    private String startUpdateTime;

    /** 结束更新时间,格式yyyy-MM-dd HH:mm:ss */
    @TableField(value = "end_update_time")
    private String endUpdateTime;

    /** 默认0:获取线上数据, 1:获取草稿数据 */
    @TableField(value = "need_edit_spu")
    private Integer needEditSpu = 0;

    /** 页号 */
    private Integer page = 1;

    /** 页面大小 */
    @TableField(value = "page_size")
    private Integer pageSize = 10;
}
