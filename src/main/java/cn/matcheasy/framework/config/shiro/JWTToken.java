package cn.matcheasy.framework.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @class JWTToken
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
public class JWTToken implements AuthenticationToken {

    private String access_token;

    private String system;

    public JWTToken(String access_token, String system) {
        this.access_token = access_token;
        this.system = system;
    }

    public Object getSystem() {
        return system;
    }

    @Override
    public Object getPrincipal() {
        return access_token;
    }

    @Override
    public Object getCredentials() {
        return access_token;
    }

}
