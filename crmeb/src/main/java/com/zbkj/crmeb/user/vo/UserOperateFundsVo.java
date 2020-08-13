package com.zbkj.crmeb.user.vo;

import io.swagger.annotations.ApiModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 资金操作
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="资金操作", description="资金操作")
public class UserOperateFundsVo implements Serializable {

    private static final long serialVersionUID=1L;
    public UserOperateFundsVo(){}
    public UserOperateFundsVo(Integer uid, String foundsType, String value) {
        this.uid = uid;
        this.foundsType = foundsType;
        this.value = value;
    }

    private Integer uid;

    private String foundsType;

    private String value;
}
