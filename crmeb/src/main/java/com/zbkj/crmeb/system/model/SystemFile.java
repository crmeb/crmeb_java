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
 * 文件对比表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_file")
@ApiModel(value="SystemFile对象", description="文件对比表")
public class SystemFile implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "文件对比ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "文件内容")
    private String cthash;

    @ApiModelProperty(value = "文价名称")
    private String filename;

    @ApiModelProperty(value = "上次访问时间")
    private String atime;

    @ApiModelProperty(value = "上次修改时间")
    private String mtime;

    @ApiModelProperty(value = "上次改变时间")
    private String ctime;


}
