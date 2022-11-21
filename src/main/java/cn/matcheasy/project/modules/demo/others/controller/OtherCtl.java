package cn.matcheasy.project.modules.demo.others.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.matcheasy.framework.annotation.RedissonLock;
import cn.matcheasy.framework.constant.RedissonLockKey;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.project.modules.demo.others.service.RetryService;
import cn.yueshutong.springbootstartercurrentlimiting.annotation.CurrentLimiter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

/**
 * @class: OtherCtl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Api(tags = "其他功能示例")
@RestController
public class OtherCtl
{

    @Autowired
    private RetryService retryService;

    @ApiOperation(value = "接口加锁，限流，权限验证", notes = "")
    @RedissonLock(lockRedisKey = RedissonLockKey.DEMO_KEY_1)//加锁
    @CurrentLimiter(QPS = 1) //限流
    @RequiresPermissions(value = {"exportUserInfo", ""}, logical = Logical.OR)
    @GetMapping("/export")
    public Result export() throws Exception
    {
        return Result.success("you can export !");
    }

    @ApiOperation(value = "接口加锁，限流，角色验证", notes = "")
    @RedissonLock(lockRedisKey = RedissonLockKey.DEMO_KEY_2)//加锁
    @CurrentLimiter(QPS = 100) //限流
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @GetMapping("/queryLog")
    public Result queryLog() throws Exception
    {
        return Result.success("you can queryLog !");
    }

    @ApiOperation(value = "生成二维码", notes = "")
    @GetMapping("/anon/generateQR")
    public void generateQR() throws Exception
    {
        QrConfig config = new QrConfig(300, 300);
        // 设置边距，既二维码和背景之间的边距
        config.setMargin(3);
        // 设置前景色，既二维码颜色（青色）
        config.setForeColor(Color.CYAN.getRGB());
        // 设置背景色（灰色）
        config.setBackColor(Color.GRAY.getRGB());
        // 高纠错级别 , L、M、Q、H
        config.setErrorCorrection(ErrorCorrectionLevel.H);
        // 生成附带logo二维码到文件，也可以到流
        QrCodeUtil.generate("http://baidu.com", QrConfig.create().setImg("D:/ezha.jpg"), FileUtil.file("D:/qrcode.jpg"));
        QrCodeUtil.generate("http://baidu.com", config, FileUtil.file("D:/qrcode2.jpg"));
    }

    @ApiOperation(value = "识别二维码", notes = "")
    @GetMapping("/anon/decodeQR")
    public Result decodeQR() throws Exception
    {
        String decode = QrCodeUtil.decode(FileUtil.file("D:/qrcode.jpg"));
        return Result.success(decode);
    }

    @ApiOperation(value = "测试jackson统一返回数据处理", notes = "")
    @GetMapping("/anon/toid")
    public Result toid(HttpServletRequest request) throws Exception
    {
        HashMap hashMap = new HashMap();
        hashMap.put("id", 32422824073367559L);
        hashMap.put("name", "");
        hashMap.put("age", null);
        hashMap.put("date", new Date());
        hashMap.put(null, "啊啊啊");
        hashMap.put("double", 1232.3232123);
        hashMap.put("bigNum", new BigDecimal("321222222222222222222222222222222222333333333333333331"));
        return Result.success(hashMap);
    }

    @ApiOperation(value = "重试机制", notes = "")
    @GetMapping("/anon/retry")
    public Result retry(HttpServletRequest request) throws Exception
    {
        retryService.retry();
        return Result.success();
    }

}
