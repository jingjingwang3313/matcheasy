package cn.matcheasy.framework.utils;

import cn.hutool.core.map.MapUtil;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @class JwtUtil
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: Jwt工具类, 生成JWT和认证
 */
@Slf4j
public class JwtUtil
{

    /**
     * 避免工具类实例化
     */
    private JwtUtil()
    {
    }

    /**
     * 生成access_token设置有效期
     */
    public static String generateToken(SysUser user, String secret, int expireTime)
    {
        //过期时间
        Date expireDate = new Date(System.currentTimeMillis() + expireTime * 1000);
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String access_token = JWT.create()
                .withHeader(map)// 添加头部
                //将基本信息放到claims
                .withClaim(ProjectConst.USER_ID, user.getUserId())
                .withClaim(ProjectConst.LOGIN_CODE, user.getLoginCode())
                .withClaim(ProjectConst.USER_NAME, user.getUserName())
                .withExpiresAt(expireDate) //过期时间
                .withIssuedAt(new Date()) //签发时间
                .sign(Algorithm.HMAC256(secret)); //SECRET加密
        return access_token;
    }

    /**
     * 验证access_token
     */
    public static boolean verifyToken(String access_token, String secret, SysUser user)
    {
        DecodedJWT jwt = null;
        try
        {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withClaim(ProjectConst.USER_ID, user.getUserId())
                    .withClaim(ProjectConst.LOGIN_CODE, user.getLoginCode())
                    .withClaim(ProjectConst.USER_NAME, user.getUserName())
                    .build();
            jwt = verifier.verify(access_token);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获得jwt中的信息,返回Map
     */
    public static Map getClaims(String access_token)
    {
        try
        {
            Map claimsValue = new HashMap();
            DecodedJWT jwt = JWT.decode(access_token);
            Map<String, Claim> claims = jwt.getClaims();
            if (MapUtil.isEmpty(claims))
            {
                return null;
            }
            else
            {
                for (Map.Entry<String, Claim> entry : claims.entrySet())
                {
                    claimsValue.put(entry.getKey(), entry.getValue().asString());
                }
                return claimsValue;
            }
        }
        catch (JWTDecodeException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得jwt中userId
     */
    public static long getUserId(String access_token)
    {
        try
        {
            DecodedJWT jwt = JWT.decode(access_token);
            return jwt.getClaim(ProjectConst.USER_ID).asLong();
        }
        catch (JWTDecodeException e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获得jwt中loginCode
     */
    public static String getLoginCode(String access_token)
    {
        try
        {
            DecodedJWT jwt = JWT.decode(access_token);
            return jwt.getClaim(ProjectConst.LOGIN_CODE).asString();
        }
        catch (JWTDecodeException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得jwt中userName
     */
    public static String getUserName(String access_token)
    {
        try
        {
            DecodedJWT jwt = JWT.decode(access_token);
            return jwt.getClaim(ProjectConst.USER_NAME).asString();
        }
        catch (JWTDecodeException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}