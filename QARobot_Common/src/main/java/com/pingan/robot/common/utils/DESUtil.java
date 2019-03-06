package com.pingan.robot.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.SecureRandom;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;

/**
 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
 */
public class DESUtil {
    //长度要是8的倍数
    private static String encWord = String.valueOf(("SentiAnalysis-DES").hashCode() * 8);

    public DESUtil() {
    }

    /**
     * 默认的加密
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String encryptStr(String str) throws Exception {
        return encrypt(str, encWord);
    }

    /**
     * 默认的解密
     *
     * @param encStr
     * @return
     * @throws Exception
     */
    public static String decryptStr(String encStr) throws Exception {
        return decrypt(encStr, encWord);
    }

    /**
     * 加密
     *
     * @param datasource String
     * @param password   String
     * @return String
     */
    public static String encrypt(String datasource, String password) throws Exception {
        byte[] enc;
        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        //创建一个密匙工厂，然后用它把DESKeySpec转换成
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(desKey);
        //Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES");
        //用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
        //现在，获取数据并加密
        //正式执行加密操作
        enc = cipher.doFinal(datasource.getBytes());
        String encStr = jdkBase64String(enc);
        return encStr;
    }

    /**
     * 解密
     *
     * @param src      String
     * @param password String
     * @return String
     * @throws Exception
     */
    public static String decrypt(String src, String password) throws Exception {
        byte[] srcByte = jdkBase64Decoder(src);
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        byte[] dec = cipher.doFinal(srcByte);
        return new String(dec);
    }

    /**
     * 使用base64解决乱码
     *
     * @param secretKey 加密后的字节码
     */
    private static String jdkBase64String(byte[] secretKey) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(secretKey);
    }

    /**
     * 使用jdk的base64 解密字符串 返回为null表示解密失败
     *
     * @throws IOException
     */
    private static byte[] jdkBase64Decoder(String str) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(str);
    }

    public static void main(String args[]) {
        //待加密内容
        String str = "abcd";
        String enc = null;
        try {
            enc = encryptStr(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("加密后：" + enc);
        //直接将如上内容解密
        try {
            String decryResult = decryptStr(enc);
            System.out.println("解密后：" + decryResult);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
