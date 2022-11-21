package cn.matcheasy.project.modules.sysuser.dto;

import cn.matcheasy.framework.commonbean.PageBean;
import cn.matcheasy.framework.validate.AddGroup;
import cn.matcheasy.framework.validate.DeleteGroup;
import cn.matcheasy.framework.validate.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @class: SysUserDto
 * @author: wangjing
 * @date: 2021/05/07
 * @desc: 用户信息表Dto对象
 */
@Data
@ApiModel(value = "SysUserDto对象")
public class SysUserDto extends PageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @NotNull(groups = {UpdateGroup.class, DeleteGroup.class}, message = "用户ID不能为空")
    private Long userId;
    /**
     * 部门ID
     */
    @ApiModelProperty(value = "部门ID")
    private Long deptId;
    /**
     * 登录账号
     */
    @ApiModelProperty(value = "登录账号")
    @NotBlank(groups = {AddGroup.class, UpdateGroup.class}, message = "登录账号不能为空")
    private String loginCode;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @NotBlank(groups = {AddGroup.class, UpdateGroup.class}, message = "密码不能为空")
    private String password;
    /**
     * 用户类型（00系统用户）
     */
    @ApiModelProperty(value = "用户类型（00系统用户）")
    private String userType;
    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String userName;
    /**
     * 用户邮箱
     */
    @ApiModelProperty(value = "用户邮箱")
    private String email;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String phone;
    /**
     * 用户性别（0男 1女 2未知）
     */
    @ApiModelProperty(value = "用户性别（0男 1女 2未知）")
    private String sex;
    /**
     * 头像路径
     */
    @ApiModelProperty(value = "头像路径")
    private String avatar;
    /**
     * 盐加密
     */
    @ApiModelProperty(value = "盐加密")
    private String salt;
    /**
     * 帐号状态（0正常 1停用）
     */
    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    private String status;
    /**
     * 最后登陆IP
     */
    @ApiModelProperty(value = "最后登陆IP")
    private String lastLoginIp;
    /**
     * 最后登陆时间
     */
    @ApiModelProperty(value = "最后登陆时间")
    private Date lastLoginDate;
    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    private String createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")//前端传字符串,Java类是Date类型,需要@DateTimeFormat注解
    private Date createTime;
    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    private String updateBy;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}
