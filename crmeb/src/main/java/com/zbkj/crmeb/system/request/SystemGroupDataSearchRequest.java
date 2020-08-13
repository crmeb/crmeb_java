package com.zbkj.crmeb.system.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 组合数据详情表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_group_data")
@ApiModel(value="SystemGroupDataSearchRequest对象", description="组合数据详情表")
public class SystemGroupDataSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;
    @ApiModelProperty(value = "关键字")
    private String keywords;

    @ApiModelProperty(value = "分组id")
    private Integer gid;

    @ApiModelProperty(value = "状态（1：开启；2：关闭；）")
    private Boolean status;

}
