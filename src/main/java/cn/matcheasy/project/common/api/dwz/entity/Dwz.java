package cn.matcheasy.project.common.api.dwz.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @class: Dwz
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Data
public class Dwz implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编码")
    private String api_code;
    @ApiModelProperty(value = "长链接")
    private String long_url;
    @ApiModelProperty(value = "短链接")
    private String short_url;
    @ApiModelProperty(value = "访问量")
    private String call_count;
    @ApiModelProperty(value = "状态")
    private String status;

}
