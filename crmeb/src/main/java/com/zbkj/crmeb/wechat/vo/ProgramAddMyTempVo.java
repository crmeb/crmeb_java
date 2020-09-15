package com.zbkj.crmeb.wechat.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProgramAddMyTempVo对象", description="小程序订阅消息模板添加")
public class ProgramAddMyTempVo {
    public ProgramAddMyTempVo() {
    }

    public ProgramAddMyTempVo(Integer tid, List<Integer> kidList, String sceneDesc) {
        this.tid = tid;
        this.kidList = kidList;
        this.sceneDesc = sceneDesc;
    }

    @ApiModelProperty(value = "模板id", required = true)
    private Integer tid;

    @ApiModelProperty(value = "开发者自行组合好的模板关键词列表，关键词顺序可以自由搭配（例如 [3,5,4] 或 [4,5,3]），最多支持5个，最少2个关键词组合", required = true)
    private List<Integer> kidList;

    @ApiModelProperty(value = "模版类型，2 为一次性订阅，3 为长期订阅")
    private String sceneDesc;
}
