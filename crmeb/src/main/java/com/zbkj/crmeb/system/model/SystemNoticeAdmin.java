package com.zbkj.crmeb.system.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 通知记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_notice_admin")
@ApiModel(value="SystemNoticeAdmin对象", description="通知记录表")
public class SystemNoticeAdmin implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "通知记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "通知类型")
    private String noticeType;

    @ApiModelProperty(value = "通知的管理员")
    private Integer adminId;

    @ApiModelProperty(value = "关联ID")
    private Integer linkId;

    @ApiModelProperty(value = "通知的数据")
    private String tableData;

    @ApiModelProperty(value = "点击次数")
    private Integer isClick;

    @ApiModelProperty(value = "访问次数")
    private Integer isVisit;

    @ApiModelProperty(value = "访问时间")
    private Integer visitTime;

    @ApiModelProperty(value = "通知时间")
    private Integer addTime;


}
