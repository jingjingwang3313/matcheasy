package cn.matcheasy.jasypt;

import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.springframework.core.env.StandardEnvironment;

/**
 * @class: JasyptTest
 * @author: wangjing
 * @date: 2021/5/18/0018
 * @desc: TODO
 */
@Slf4j
public class JasyptTest
{

    @Test
    public void test()
    {
        // 对应配置文件中配置的Jasypt密钥
        System.setProperty("jasypt.encryptor.password", "123456");
        StringEncryptor stringEncryptor = new DefaultLazyEncryptor(new StandardEnvironment());

        // redis
        log.info("redis加密： " + stringEncryptor.encrypt("zbhlw20201224"));
        log.info("redis解密： " + stringEncryptor.decrypt("lU2+Cl0eJ1od8cR362w4icZdgkdqjE9wMbA/LWqMunxb6hi2YFNOY8TwfqqEHCrb"));

        // rabbitmq
        log.info("rabbitmq加密： " + stringEncryptor.encrypt("guest"));
        log.info("rabbitmq解密： " + stringEncryptor.decrypt("Hvm6ecqsSiwE6UcAKV7De243Y+lL5HCAmkxodJC17i13OFG/BnEHMwxeqgNebW9R"));

        // elasticsearch
        log.info("elasticsearch加密： " + stringEncryptor.encrypt("elastic"));
        log.info("elasticsearch解密： " + stringEncryptor.decrypt("2mCVSKB7kgA+kz1jYJJIg8S4srvpBME+ECQCk278GZ861Y/QzGxGsZS3bdul56HA"));

        // mongo
        log.info("mongo加密： " + stringEncryptor.encrypt("mongo123"));
        log.info("mongo解密： " + stringEncryptor.decrypt("qmp+cAHSr/CZlNbY8+zuLfuSrXTHvnykUypM1ym9F0X6AzJ7ajAFQTLECSIRyW+3"));

        // 通用密码
        log.info("通用密码加密： " + stringEncryptor.encrypt("123456"));
        log.info("通用密码解密： " + stringEncryptor.decrypt("cn66zHDpMK6qy0g1sN2lqw0MTO2i7rRkIAK8J6aKbhI9mxb1gi86x9RYRNSuNxZo"));

    }

}
