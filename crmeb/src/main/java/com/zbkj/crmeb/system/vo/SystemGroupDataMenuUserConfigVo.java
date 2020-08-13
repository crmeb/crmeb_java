package com.zbkj.crmeb.system.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 签到记录
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemGroupDataSignConfigVo对象", description="签到记录")
public class SystemGroupDataMenuUserConfigVo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id")
    private Integer id;

    @ApiModelProperty(value = "显示文字")
    private String name;

    @ApiModelProperty(value = "图片")
    private String pic;

    @ApiModelProperty(value = "跳转地址")
    private String url;
}
