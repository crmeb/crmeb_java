package com.zbkj.common.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 秒杀商品管理Request对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreSeckillMangerRequest对象", description="")
public class StoreSeckillMangerRequest {

    @ApiModelProperty(value = "秒杀配置id")
    private Integer id;

    @ApiModelProperty(value = "秒杀时段名称")
    @NotBlank(message = "秒杀时段名称不能为空")
    @Length(max = 255, message = "秒杀时段名称不能超过255个字符")
    private String name;

//    @ApiModelProperty(value = "秒杀开始时间段")
//    private Integer startTime;
//
//    @ApiModelProperty(value = "秒杀结束时间段")
//    private Integer endTime;

    @ApiModelProperty(value = "秒杀结束时间段")
    @NotBlank(message = "秒杀时间段不能为空")
    private String time; // 接收参数一个字段，入库时分割为startTime/endTime

    @ApiModelProperty(value = "主图")
    private String img;

    @ApiModelProperty(value = "轮播图")
    @NotBlank(message = "轮播图不能为空")
    private String silderImgs;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态 0=关闭 1=开启")
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "未知的状态")
    private Integer status;

    @ApiModelProperty(value = "0未删除1已删除")
    private Boolean isDel;
}
