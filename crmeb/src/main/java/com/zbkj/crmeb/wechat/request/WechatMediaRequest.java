package com.zbkj.crmeb.wechat.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 微信回复表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wechat_media")
@ApiModel(value="WechatMedia对象", description="微信回复表")
public class WechatMediaRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "回复类型")
    private String type;

    @ApiModelProperty(value = "文件路径")
    private String path;

    @ApiModelProperty(value = "微信服务器返回的id")
    private String mediaId;

    @ApiModelProperty(value = "地址")
    private String url;

    @ApiModelProperty(value = "是否永久或者临时 0永久1临时")
    private Boolean temporary;

}
