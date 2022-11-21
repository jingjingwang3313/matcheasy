package cn.matcheasy.framework.utils;

import com.alibaba.fastjson.JSON;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * @class DesUtil
 * @author: wangjing
 * @date: 2020/11/7/0007
 * @desc: 对称加密算法, DES密钥比较短
 */
public class DesUtil {

    private final static String DES = "DES";

    private final static String ENCODE = "UTF-8";

    private final static String defaultKey = "jiami89757";

    /**
     * 避免工具类实例化
     */
    private DesUtil() {
    }

    /**
     * 使用默认key 加密
     */
    public static String encrypt(String data) throws Exception {
        if (data == null)
            return null;
        byte[] bt = encrypt(data.getBytes(ENCODE), defaultKey.getBytes(ENCODE));
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    /**
     * 使用默认key 解密
     */
    public static String decrypt(String data) throws IOException, Exception {
        if (data == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = decrypt(buf, defaultKey.getBytes(ENCODE));
        return new String(bt, ENCODE);
    }

    /**
     * Description 根据键值进行加密
     */
    public static String encrypt(String data, String key) throws Exception {
        if (data == null)
            return null;
        byte[] bt = encrypt(data.getBytes(ENCODE), key.getBytes(ENCODE));
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    /**
     * Description 根据键值进行解密
     */
    public static String decrypt(String data, String key) throws Exception {
        if (data == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = decrypt(buf, key.getBytes(ENCODE));
        return new String(bt, ENCODE);
    }

    /**
     * Description 根据键值进行加密
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }

    /**
     * Description 根据键值进行解密
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> map = new HashMap<>(2);
        map.put("name", "阿尔法狗");
        map.put("des", "编号89757");

        String en = encrypt(JSON.toJSONString(map));
        System.out.println(en);
        System.out.println(decrypt(en));
        System.out.println("---------------------------------------------------------------------");
        String en1 = encrypt(JSON.toJSONString(map), "jiemi89757");
        System.out.println(en1);
        System.out.println(decrypt((en1), "jiemi89757"));
        System.out.println("---------------------------------------------------------------------");

        byte[] en2 = encrypt(JSON.toJSONString(map).getBytes(), "hellokey110".getBytes());
        System.out.println(en2);
        System.out.println(new String(decrypt(en2, "hellokey110".getBytes()), ENCODE));
    }

}