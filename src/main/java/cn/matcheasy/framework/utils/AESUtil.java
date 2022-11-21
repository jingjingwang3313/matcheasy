package cn.matcheasy.framework.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @class AESUtil
 * @author: wangjing
 * @date: 2020/11/7/0007
 * @desc: 对称加密算法, AES密钥比较长
 */
public class AESUtil
{

    private static final String KEY_AES = "AES";

    /**
     * 避免工具类实例化
     */
    private AESUtil()
    {
    }

    /**
     * @param src
     * @param key 长度16
     * @return
     * @throws Exception
     */
    public static String encrypt(String src, String key) throws Exception
    {
        if (key == null || key.length() != 16)
        {
            throw new Exception("key不满足条件");
        }
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(src.getBytes());
        return byte2hex(encrypted);
    }

    /**
     * @param src
     * @param key 长度16
     * @return
     * @throws Exception
     */
    public static String decrypt(String src, String key) throws Exception
    {
        if (key == null || key.length() != 16)
        {
            throw new Exception("key不满足条件");
        }
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] encrypted1 = hex2byte(src);
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original);
        return originalString;
    }

    public static byte[] hex2byte(String strhex)
    {
        if (strhex == null)
        {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1)
        {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++)
        {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }

    public static String byte2hex(byte[] b)
    {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < b.length; n++)
        {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
            {
                hs.append("0").append(stmp);
            }
            else
            {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }

    public static void main(String[] args) throws Exception
    {
        String content = "testContext";
        System.out.println("原内容 = " + content);

        String encrypt = AESUtil.encrypt(content, "key_value_length");
        System.out.println("加密后 = " + encrypt);

        String decrypt = AESUtil.decrypt(encrypt, "key_value_length");
        System.out.println("解密后 = " + decrypt);

        /*exit:
        for (int i = 0; i < 10; i++)
        {
            System.out.println("外循环");
            for (int j = 0; j < 10; j++)
            {
                System.out.println("内循环");
                break exit;
            }
        }*/
    }

}
