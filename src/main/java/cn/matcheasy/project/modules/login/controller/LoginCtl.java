package cn.matcheasy.project.modules.login.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.log.MatcheasyLog;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.framework.utils.ComU;
import cn.matcheasy.framework.utils.JwtUtil;
import cn.matcheasy.framework.utils.RedisService;
import cn.matcheasy.framework.utils.RegExUtil;
import cn.matcheasy.project.common.api.mail.entity.Email;
import cn.matcheasy.project.common.api.mail.service.MailService;
import cn.matcheasy.project.common.api.sms.service.SmsService;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import cn.matcheasy.project.modules.sysuser.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @class: LoginCtl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Api(tags = "登录管理")
@RestController
public class LoginCtl
{

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

    /**
     * verifyCode过期时间,单位为秒
     */
    @Value("${verifyCode.expireTime}")
    private int verifyCodeExpireTime;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private MailService mailService;

    /**
     * handled by wangjing at 2020/11/05/0011 13:22
     *
     * @desc 用户注册
     */
    @ApiOperation(value = "用户注册", notes = "创建用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginCode", value = "用户登陆码，必填", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码，必填", required = true, dataType = "string"),
            @ApiImplicitParam(name = "confirmPassword", value = "确认密码，必填", required = true, dataType = "string"),
            @ApiImplicitParam(name = "userName", value = "用户名称，非必填", required = false, dataType = "string")
    })
    @CrossOrigin //跨域
    @PostMapping("/anon/register")
    public Result register(HttpServletRequest request) throws Exception
    {
        Map result = new HashMap();
        Map map = ComU.getRequestMap(request);
        String loginCode = MapUtil.getStr(map, ProjectConst.LOGIN_CODE);
        String password = MapUtil.getStr(map, ProjectConst.PASSWORD);
        String confirmPassword = MapUtil.getStr(map, ProjectConst.CONFIRM_PASSWORD);
        String userName = MapUtil.getStr(map, ProjectConst.USER_NAME);
        if (StringUtils.isEmpty(loginCode))
        {
            return Result.error("注册失败,用户登陆码不能为空！");
        }
        if (StringUtils.isEmpty(password))
        {
            return Result.error("注册失败,密码不能为空 ！");
        }
        if (!StringUtils.equals(password, confirmPassword))
        {
            return Result.error("注册失败,两次输入密码不一致 ！");
        }
        // MD5加密
        String encodePwd = DigestUtils.md5DigestAsHex(password.getBytes());
        SysUser user = new SysUser();
        user.setLoginCode(loginCode);
        user.setPassword(encodePwd);
        user.setUserName(userName);
        boolean boo = sysUserService.save(user);
        if (boo)
        {
            return Result.success("注册成功!");
        }
        else
        {
            return Result.success("注册失败!");
        }
    }

    /**
     * handled by wangjing at 2020/11/05/0011 13:22
     *
     * @desc 请求生成 token
     */
    @ApiOperation(value = "用户登录", notes = "生成token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginCode", value = "登录码，必填", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码，必填", required = true, dataType = "string")
    })
    @CrossOrigin //跨域
    @PostMapping("/anon/generateToken")
    public Result generateToken(HttpServletRequest request) throws Exception
    {
        Map result = new HashMap();
        Map map = ComU.getRequestMap(request);
        String loginCode = MapUtil.getStr(map, ProjectConst.LOGIN_CODE);
        String password = MapUtil.getStr(map, ProjectConst.PASSWORD);
        if (StringUtils.isEmpty(loginCode))
        {
            return Result.error("登录失败,用户登陆码不能为空！");
        }
        if (StringUtils.isEmpty(password))
        {
            return Result.error("登录失败,密码不能为空 ！");
        }
        SysUser user = sysUserService.selectUserInfoByLoginCode(loginCode);
        if (ObjectUtil.isEmpty(user))
        {
            return Result.error("登录失败,用户不存在!");
        }
        else
        {
            String dbPwd = user.getPassword();
            String encodePwd = DigestUtils.md5DigestAsHex(password.getBytes());
            boolean matchesResult = StringUtils.equals(dbPwd, encodePwd);
            if (!matchesResult)
            {
                return Result.error("登录失败,密码错误!");
            }
            else
            {
                String access_token = JwtUtil.generateToken(user, secret, expireTime);
                MatcheasyLog.info(StrUtil.format("生成access_token：{},过期时间expireTime：{}s", access_token, expireTime));
                // access_token放入redis
                redisService.setCacheObject(access_token, access_token, expireTime, TimeUnit.SECONDS);
                result.put(ProjectConst.ACCESS_TOKEN, access_token);
                result.put(ProjectConst.USER_NAME, user.getUserName());
                result.put("expireTime", expireTime + " s");
                return Result.success(result);
            }
        }
    }

    /**
     * handled by wangjing at 2020/11/10/0010 15:36
     *
     * @desc 使 token 失效
     */
    @ApiOperation(value = "用户退出", notes = "失效token")
    @ApiImplicitParam(name = "access_token", value = "要使失效的token，必填", required = true, dataType = "string")
    @CrossOrigin //跨域
    @GetMapping("/invalidToken")
    public Result invalidToken(HttpServletRequest request) throws Exception
    {
        String access_token = MapUtil.getStr(ComU.getRequestMap(request), ProjectConst.ACCESS_TOKEN);
        redisService.deleteObject(access_token);
        return Result.success("access_token 已失效!");
    }

    /**
     * Handled by wangjing at 2020/12/24/0024 17:24
     *
     * @Desc 发送验证码(手机, 邮箱)
     */
    @ApiOperation(value = "验证码", notes = "获取验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phoneOrEmail", value = "接收验证码的手机/邮箱，必填", required = true, dataType = "string")
    })
    @CrossOrigin //跨域
    @GetMapping("/anon/verifyCode")
    public Result verifyCode(HttpServletRequest request) throws Exception
    {
        Map result = new HashMap();
        Map map = ComU.getRequestMap(request);
        String phoneOrEmail = MapUtil.getStr(map, ProjectConst.PHONE_OR_EMAIL);
        if (StringUtils.isEmpty(phoneOrEmail)
                || (!RegExUtil.isEmail(phoneOrEmail) && !RegExUtil.isPhoneNumber(phoneOrEmail)))
        {
            return Result.error("获取验证码失败,手机号或邮箱有误!");
        }
        String code = ComU.getRandomBylength(6);
        String content = null;
        redisService.setCacheObject(phoneOrEmail, code, verifyCodeExpireTime, TimeUnit.SECONDS);
        result.put("verifyCode", code);
        content = "验证码：【" + code + "】,有效期：" + verifyCodeExpireTime + " s。";
        // 发送验证码,两种情况(手机,邮箱)
        if (RegExUtil.isPhoneNumber(phoneOrEmail))
        {
            String ret = smsService.sendSMS(content, phoneOrEmail);
            MatcheasyLog.info(StrUtil.format("发送短信响应信息: {}", ret));
            if (StringUtils.isNotEmpty(ret))
            {
                return Result.error(ret);
            }
        }
        else if (RegExUtil.isEmail(phoneOrEmail))
        {
            List toAddr = new ArrayList();
            toAddr.add(phoneOrEmail);
            Email email = new Email();
            email.setSubject("验证码");
            email.setContent(content);
            email.setToAddress(toAddr);
            mailService.sendMail(email);
        }
        result.put("expireTime", verifyCodeExpireTime + " s。");
        MatcheasyLog.info(StrUtil.format("发送验证码到: {},发送内容: {}", phoneOrEmail, content));
        return Result.success(result);
    }

}
