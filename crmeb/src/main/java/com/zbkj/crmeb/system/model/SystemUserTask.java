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
 * 等级任务设置
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_user_task")
@ApiModel(value="SystemUserTask对象", description="等级任务设置")
public class SystemUserTask implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "配置原名")
    private String realName;

    @ApiModelProperty(value = "任务类型")
    private String taskType;

    @ApiModelProperty(value = "限定数")
    private Integer number;

    @ApiModelProperty(value = "等级id")
    private Integer levelId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否显示")
    private Boolean isShow;

    @ApiModelProperty(value = "是否务必达成任务,1务必达成,0=满足其一")
    private Boolean isMust;

    @ApiModelProperty(value = "任务说明")
    private String illustrate;

    @ApiModelProperty(value = "新增时间")
    private Integer addTime;


}
