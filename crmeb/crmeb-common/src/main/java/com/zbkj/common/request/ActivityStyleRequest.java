package com.zbkj.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 活动维护对象
 * @Auther: 大粽子
 * @Date: 2023/1/5 12:15
 * @Description: 活动controller request
 */
@Data
public class ActivityStyleRequest {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "活动名称")
    @NotEmpty(message = "活动名称不能为空")
    private String name;

    @ApiModelProperty(value = "活动类型0=活动边框，1=活动背景")
    private Boolean type;

    @ApiModelProperty(value = "开始时间")
    @NotEmpty(message = "开始时间不能为空")
    private String starttime;

    @ApiModelProperty(value = "结束时间")
    @NotEmpty(message = "结束时间不能为空")
    private String endtime;

    @ApiModelProperty(value = "活动边框素材地址")
    @NotEmpty(message = "活动素材 不能为空")
    private String style;

    @ApiModelProperty(value = "状态 0=关闭1=开启")
    private Boolean status;

    @ApiModelProperty(value = "商品参与类型 0=全部商品参与，1=指定商品参与，2=指定品牌参与，3=指定商品分类参与")
    @NotNull(message = "活动参与类型不能为空")
    @Range(min = 0, max = 4, message = "商品参与类型 0=全部商品参与，1=指定商品参与，2=指定品牌参与，3=指定商品分类参与， 4=指定商户产品")
    private Integer method;

    @ApiModelProperty(value = "参与活动商品")
    private String products;

}
