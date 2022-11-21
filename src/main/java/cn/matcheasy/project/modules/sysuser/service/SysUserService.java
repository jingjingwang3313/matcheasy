package cn.matcheasy.project.modules.sysuser.service;

import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @class: SysUserService
 * @author: wangjing
 * @date: 2021/05/07
 * @desc: 用户信息表Service
 */
public interface SysUserService extends IService<SysUser> {

    IPage<SysUser> selectListPage(IPage<SysUser> page, SysUser sysUser) throws Exception;

    List<ConcurrentHashMap<String, Object>> selectUserPowerByUserId(long loginCode) throws Exception;

    SysUser selectUserInfoByLoginCode(String loginCode) throws Exception;
}
