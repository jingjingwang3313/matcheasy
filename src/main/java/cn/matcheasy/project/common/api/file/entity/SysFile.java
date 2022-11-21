package cn.matcheasy.project.common.api.file.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @class: SysFile
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Data
public class SysFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件ID")
    private Long fileId;

    @ApiModelProperty(value = "上传文件名 ")
    private String fileName;

    @ApiModelProperty(value = "存储真实文件名 ")
    private String fileRealName;

    @ApiModelProperty(value = "文件访问地址 ")
    private String accessUrl;

    @ApiModelProperty(value = "文件存储绝对路径 ")
    private String storagePath;

    @ApiModelProperty(value = "文件存储相对路径 ")
    private String relativePath;

    @ApiModelProperty(value = "文件类型 ")
    private String fileType;

    @ApiModelProperty(value = "文件大小 ")
    private Long fileSize;

    @ApiModelProperty(value = "状态：1-有效，2-删除 ")
    private Byte status;

    @ApiModelProperty(value = "上传者 ")
    private Long createBy;

    @ApiModelProperty(value = "上传时间 ")
    private Date createTime;

}