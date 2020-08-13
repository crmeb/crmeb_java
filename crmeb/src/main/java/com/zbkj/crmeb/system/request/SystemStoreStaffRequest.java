package com.zbkj.crmeb.system.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * <p>
 * 门店店员表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_store_staff")
@ApiModel(value="SystemStoreStaff对象", description="门店店员表")
public class SystemStoreStaffRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "微信用户id")
    @Min(value = 1, message = "请选择用户")
    private Integer uid;

    @ApiModelProperty(value = "店员头像")
    private String avatar;

    @ApiModelProperty(value = "提货点id")
    @Min(value = 1, message = "请选择提货点")
    private Integer storeId;

    @ApiModelProperty(value = "店员名称")
    private String staffName;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "核销开关")
    private boolean verifyStatus = false;

    @ApiModelProperty(value = "状态")
    private boolean status = false;


}
