package com.zbkj.crmeb.seckill.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname StoreSeckillSearchRequest
 * @Description 秒杀搜索参数
 * @Date 9/17/20 4:21 下午
 * @Created by stivepeim
 */
@Data
public class StoreSeckillSearchRequest {

    @ApiModelProperty(value = "搜索关键字 商品id或者名称")
    private String keywords;

    @ApiModelProperty(value = "秒杀时段")
    private Integer timeId;

    @ApiModelProperty(value = "是否显示关键字 0/1")
    private Integer status;
}

