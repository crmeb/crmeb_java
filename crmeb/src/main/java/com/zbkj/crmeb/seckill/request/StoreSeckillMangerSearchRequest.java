package com.zbkj.crmeb.seckill.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品秒杀配置
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
@TableName("StoreSeckillMangerSearchRequest")
@ApiModel(value="StoreSeckillMangerSearchRequest", description="商品秒杀配置")
public class StoreSeckillMangerSearchRequest {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "秒杀名称")
    private String name;

//    @ApiModelProperty(value = "秒杀开始时间段")
//    private Integer startTime;
//
//    @ApiModelProperty(value = "秒杀结束时间段")
//    private Integer endTime;

    @ApiModelProperty(value = "秒杀结束时间段")
//    @NotBlank(message = "秒杀时间段不能为空")
    private String time; // 接收参数一个字段，入库时分割为startTime/endTime

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态 0=关闭 1=开启")
    private Integer status;

    @ApiModelProperty(value = "0未删除1已删除")
    private Boolean isDel;
}
