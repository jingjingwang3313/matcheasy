package cn.matcheasy.project.modules.sysuser.controller;

import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.framework.utils.BeanCopyUtil;
import cn.matcheasy.framework.validate.AddGroup;
import cn.matcheasy.framework.validate.UpdateGroup;
import cn.matcheasy.project.common.base.controller.BaseController;
import cn.matcheasy.project.modules.sysuser.dto.SysUserDto;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import cn.matcheasy.project.modules.sysuser.service.SysUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @class: SysUserController
 * @author: wangjing
 * @date: 2021/05/07
 * @desc: 用户信息表Controller
 */
@Slf4j
@RestController
@RequestMapping("/sysUser")
@Api(description = "SysUserController", tags = {"用户信息表"})
public class SysUserCtl extends BaseController
{

    @Autowired
    private SysUserService sysUserService;

    /**
     * 自定义条件分页查询
     *
     * @param sysUserDto
     * @return Result
     */
    @ApiOperation(value = "自定义条件分页查询", notes = "自定义条件分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysUserDto", value = "查询条件", required = false)
    })
    @GetMapping("/selectListPage")
    public Result selectListPage(SysUserDto sysUserDto) throws Exception
    {
        Map resultMap = new HashMap();
        SysUser sysUser = BeanCopyUtil.convertBean(sysUserDto, SysUser.class);
        IPage<SysUser> page = startPage(sysUserDto.getPageNum(), sysUserDto.getPageSize());
        IPage<SysUser> pageRet = sysUserService.selectListPage(page, sysUser);
        resultMap.put(ProjectConst.RESULT, returnPage(pageRet));
        return Result.success(resultMap);
    }

    /**
     * 条件分页查询
     *
     * @param sysUserDto
     * @return Result
     */
    @ApiOperation(value = "条件分页查询", notes = "条件分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysUserDto", value = "查询条件", required = false)
    })
    @GetMapping("/selectPage")
    public Result selectPage(SysUserDto sysUserDto) throws Exception
    {
        Map resultMap = new HashMap();
        SysUser sysUser = BeanCopyUtil.convertBean(sysUserDto, SysUser.class);
        IPage<SysUser> page = startPage(sysUserDto.getPageNum(), sysUserDto.getPageSize());
        IPage<SysUser> pageRet = sysUserService.page(page, Wrappers.query(sysUser));
        resultMap.put(ProjectConst.RESULT, returnPage(pageRet));
        return Result.success(resultMap);
    }

    /**
     * 条件查询所有
     *
     * @param sysUserDto
     * @return Result
     */
    @ApiOperation(value = "条件查询所有")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysUserDto", value = "查询条件", required = false)
    })
    @GetMapping("/selectAll")
    public Result selectAll(SysUserDto sysUserDto) throws Exception
    {
        Map resultMap = new HashMap();
        SysUser sysUser = BeanCopyUtil.convertBean(sysUserDto, SysUser.class);
        resultMap.put(ProjectConst.RESULT, sysUserService.list(Wrappers.query(sysUser)));
        return Result.success(resultMap);
    }

    /**
     * 通过id查询
     *
     * @param userId id
     * @return Result
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "主键id", required = true)
    })
    @GetMapping("/{userId}")
    public Result getSysUser(@PathVariable("userId") Long userId) throws Exception
    {
        Map resultMap = new HashMap();
        SysUser sysUser = sysUserService.getById(userId);
        resultMap.put(ProjectConst.RESULT, sysUser);
        return Result.success(resultMap);
    }

    /**
     * 新增
     *
     * @param sysUserDto
     * @return Result
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/save")
    public Result saveSysUser(@Validated({AddGroup.class}) @RequestBody SysUserDto sysUserDto) throws Exception
    {
        SysUser sysUser = BeanCopyUtil.convertBean(sysUserDto, SysUser.class);
        String encodePwd = DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes());
        sysUser.setPassword(encodePwd);
        boolean b = sysUserService.save(sysUser);
        return Result.success();
    }

    /**
     * 修改
     *
     * @param sysUserDto
     * @return Result
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PutMapping("/update")
    public Result updateSysUser(@Validated({UpdateGroup.class}) @RequestBody SysUserDto sysUserDto) throws Exception
    {
        SysUser sysUser = BeanCopyUtil.convertBean(sysUserDto, SysUser.class);
        String encodePwd = DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes());
        sysUser.setPassword(encodePwd);
        boolean b = sysUserService.updateById(sysUser);
        return Result.success();
    }

    /**
     * 通过id删除
     *
     * @param userId id
     * @return Result
     */
    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "主键id", required = true)
    })
    @DeleteMapping("/{userId}")
    public Result deleteSysUser(@PathVariable("userId") Long userId) throws Exception
    {
        boolean b = sysUserService.removeById(userId);
        return Result.success();
    }

}
