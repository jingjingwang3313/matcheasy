package cn.matcheasy.framework.config.shiro;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.log.MatcheasyLog;
import cn.matcheasy.framework.result.ResultEnum;
import cn.matcheasy.framework.utils.JwtUtil;
import cn.matcheasy.framework.utils.RedisService;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import cn.matcheasy.project.modules.sysuser.service.SysUserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @class UserRealm
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Component
public class UserRealm extends AuthorizingRealm
{
    private String system;
    /**
     * 密钥
     */
    @Value("${access_token.secret}")
    private String secret;

    /**
     * 过期时间,单位为秒
     */
    @Value("${access_token.expireTime}")
    private int expireTime;

    @Autowired
    private RedisService redisService;

    //shiro和Aspect冲突是Spring bean实例化前后顺序问题,可延迟初始化
    @Lazy
    @Autowired
    private SysUserService sysUserService;

    /**
     * 必须重写此方法
     */
    @Override
    public boolean supports(AuthenticationToken access_token)
    {
        this.system = (String) ((JWTToken) access_token).getSystem();
        return access_token instanceof JWTToken;
    }

    /**
     * 默认使用此方法进行登录验证，错误抛出异常即可
     */
    @SneakyThrows
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException
    {
        MatcheasyLog.info(StrUtil.format("Current platform {}!", system));
        String access_token = (String) authenticationToken.getCredentials();
        // 验证access_token是否过期
        String cacheToken = redisService.getCacheObject(access_token);
        if (StringUtils.isEmpty(cacheToken))
        {
            throw new AuthenticationException(ResultEnum.TOKEN_EXPIRED.getMsg());
        }
        else
        {
            MatcheasyLog.info(StrUtil.format("请求access_token: {}", cacheToken));
        }
        SysUser user = sysUserService.getById(JwtUtil.getUserId(cacheToken));
        if (ObjectUtil.isEmpty(user))
        {
            throw new AuthenticationException(ResultEnum.ACCOUNT_EXCEPTION.getMsg());
        }
        if (!JwtUtil.verifyToken(cacheToken, secret, user))
        {
            // redis中token未过期,刷新access_token
            cacheToken = JwtUtil.generateToken(user, secret, expireTime);
        }
        // 刷新access_token过期时间
        redisService.setCacheObject(access_token, cacheToken, expireTime, TimeUnit.SECONDS);
        return new SimpleAuthenticationInfo(cacheToken, access_token, "UserRealm");
    }

    /**
     * 当需要检测用户权限的时候调用此方法，如checkRole,checkPermission
     */
    @SneakyThrows
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
    {
        MatcheasyLog.info(StrUtil.format("Current platform {}!", system));
        long userId = JwtUtil.getUserId(principals.toString());
        String userName = JwtUtil.getUserName(principals.toString());
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        List<ConcurrentHashMap<String, Object>> powerList = sysUserService.selectUserPowerByUserId(userId);
        if (CollectionUtil.isNotEmpty(powerList))
        {
            MatcheasyLog.info(StrUtil.format("用户: {}-{} 的权限集合: {}", userId, userName, powerList.toString()));
            powerList.parallelStream().forEachOrdered(powerMap ->
            {
                //添加角色
                simpleAuthorizationInfo.addRole(MapUtil.getStr(powerMap, ProjectConst.ROLE));
                //添加权限
                simpleAuthorizationInfo.addStringPermission(MapUtil.getStr(powerMap, ProjectConst.PERMISSION));
            });
        }
        return simpleAuthorizationInfo;
    }

}

