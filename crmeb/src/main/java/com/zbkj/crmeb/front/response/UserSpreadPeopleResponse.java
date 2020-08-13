package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 推广用户
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserSpreadPeopleResponse对象", description="推广用户")
public class UserSpreadPeopleResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "一级推广人人数")
    private Integer total = 0;

    @ApiModelProperty(value = "二级推广人人数")
    private Integer totalLevel = 0;

    @ApiModelProperty(value = "推广人列表")
    private List<UserSpreadPeopleItemResponse> spreadPeopleList;
}
