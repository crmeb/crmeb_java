package com.common;

import com.constants.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageParamRequest {

    @ApiModelProperty(value = "页码", example= Constants.DEFAULT_PAGE + "")
    private int page = Constants.DEFAULT_PAGE;

    @ApiModelProperty(value = "每页数量", example = Constants.DEFAULT_LIMIT + "")
    private int limit = Constants.DEFAULT_LIMIT;

}
