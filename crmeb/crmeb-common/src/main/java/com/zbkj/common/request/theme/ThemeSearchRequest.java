package com.zbkj.common.request.theme;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 主题搜索请求
 */
@Data
@ApiModel(value = "ThemeSearchRequest对象", description = "主题搜索请求")
public class ThemeSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主题名称，模糊匹配title和info")
    private String title;

    @ApiModelProperty(value = "主题类型，0:自建主题，1:广场主题")
    private Integer type;

    @ApiModelProperty(value = "页面类型：theme主题，micro微页面")
    @JsonProperty(value = "page_type")
    private String pageType;

    @ApiModelProperty(value = "是否删除")
    @JsonProperty(value = "is_del")
    private Integer isDel = 0;

    @ApiModelProperty(value = "是否使用")
    @JsonProperty(value = "is_use")
    private Integer isUse;

    public void setPage_type(String pageType) {
        this.pageType = pageType;
    }

    public void setIs_del(Integer isDel) {
        this.isDel = isDel;
    }

    public void setIs_use(Integer isUse) {
        this.isUse = isUse;
    }
}
