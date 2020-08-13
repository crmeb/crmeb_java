package com.zbkj.crmeb.user.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户任务完成记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_task_finish")
@ApiModel(value="UserTaskFinish对象", description="用户任务完成记录表")
public class UserTaskFinish implements Serializable {

    private static final long serialVersionUID=1L;
    public UserTaskFinish(){}
    public UserTaskFinish(Integer taskId, Integer uid, Boolean status) {
        this.taskId = taskId;
        this.uid = uid;
        this.status = status;
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务id")
    private Integer taskId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "是否有效")
    private Boolean status;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
