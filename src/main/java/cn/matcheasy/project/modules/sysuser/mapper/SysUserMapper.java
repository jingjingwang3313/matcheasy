package cn.matcheasy.project.modules.sysuser.mapper;

import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @class: SysUserMapper
 * @author: wangjing
 * @date: 2021/05/07
 * @desc: 用户信息表Mapper
 */
@Component
public interface SysUserMapper extends BaseMapper<SysUser> {

    IPage<SysUser> selectListPage(IPage<SysUser> page, @Param("sysUser") SysUser sysUser);

    List<ConcurrentHashMap<String, Object>> selectUserPowerByUserId(long userId);

}
