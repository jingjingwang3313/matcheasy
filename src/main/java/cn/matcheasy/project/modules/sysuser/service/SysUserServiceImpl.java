package cn.matcheasy.project.modules.sysuser.service;

import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import cn.matcheasy.project.modules.sysuser.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @class: SysUserServiceImpl
 * @author: wangjing
 * @date: 2021/05/07
 * @desc: 用户信息表ServiceImpl
 */
@Slf4j
@Service
//@DataSourceRoute(name = DataSourceKey.DEFAULT_CLUSTER)
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<SysUser> selectListPage(IPage<SysUser> page, SysUser sysUser) throws Exception {
        return sysUserMapper.selectListPage(page, sysUser);
    }

    @Override
    @Cacheable(value = "powerCache", condition = "", unless = "#result == null || #result.size() == 0")
    public List<ConcurrentHashMap<String, Object>> selectUserPowerByUserId(long userId) throws Exception {
        return sysUserMapper.selectUserPowerByUserId(userId);
    }

    @Override
    public SysUser selectUserInfoByLoginCode(String loginCode) throws Exception {
        SysUser user = new LambdaQueryChainWrapper<>(sysUserMapper)
                .eq(SysUser::getLoginCode, loginCode)
                .one();
        return user;
    }

}
