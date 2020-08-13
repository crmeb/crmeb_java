package com.zbkj.crmeb.user.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 签到记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_sign")
@ApiModel(value="UserSign对象", description="签到记录表")
public class UserSignMonthVo implements Serializable {

    private static final long serialVersionUID=1L;

    public UserSignMonthVo() {}
    public UserSignMonthVo(String month, List<UserSignVo> list) {
        this.month = month;
        this.list = list;
    }

    @ApiModelProperty(value = "月")
    private String month;

    @ApiModelProperty(value = "签到列表")
    private List<UserSignVo> list;
}
