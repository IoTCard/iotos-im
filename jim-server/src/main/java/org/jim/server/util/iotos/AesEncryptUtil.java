package org.jim.server.util.iotos;
/**
 * AES 128bit 加密解密工具类
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;


public class AesEncryptUtil {

    //使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同！
    private static String KEY = "urlwww.iotos.top";

    private static String IV = "WeiXin:iotos_top";


    /**
     * 加密方法
     * @param data  要加密的数据
     * @param key 加密key
     * @param iv 加密iv
     * @return 加密的结果
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"算法/模式/补码方式"
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes("utf-8");
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes("utf-8"), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes("utf-8"));
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return new Base64().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密方法
     * @param data 要解密的数据
     * @param key  解密key
     * @param iv 解密iv
     * @return 解密的结果
     * @throws Exception
     */
    public static String desEncrypt(String data, String key, String iv) throws Exception {
        try {
            byte[] encrypted1 = new Base64().decode(data);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes("utf-8"), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes("utf-8"));
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original,"utf-8");
            return originalString.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用默认的key和iv加密
     * @param data
     * @return
     * @throws Exception
     */
    public static String encrypt(String data) throws Exception {
        return encrypt(data, KEY, IV);
    }

    /**
     * 使用默认的key和iv解密
     * @param data
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String data) throws Exception {
        return desEncrypt(data, KEY, IV);
    }

    public static String byteToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 解密
     *
     * @param pwdStr
     * @return
     */
    public static JSONObject getParameter(String pwdStr){
        JSONObject parameter = null;
        if (pwdStr != null) {
            pwdStr = pwdStr.replace("%2F", "/");//转义 /
            pwdStr = pwdStr.replace(" ", "+");//转义 [ ]  》 +
        }
        try {
            pwdStr = AesEncryptUtil.desEncrypt(pwdStr);
        }catch (Exception e){
            System.err.println("MQAide getParameter 异常 pwdStr "+pwdStr+"  <br/>  e:{} "+e.getMessage());
        }
        parameter = JSON.parseObject(pwdStr);
        return parameter;
    }

    public static void main(String[] args) throws Exception {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("token","eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjY0YzY3NmM0LTM4MWQtNDZkZi04ZTIwLTQwYzQ4NzYxNjFjNSJ9.7CdWRbkp3DqLEpDzbQMZit6Qks6KQbmEx65aNuMrYM84hMUyzVNUQs1rtrfmWZcEb35OtctBpH1k7_w-KHeKmA");
        String pwdStr = AesEncryptUtil.encrypt(JSON.toJSONString(paramMap));
        System.out.println(pwdStr);
    }
}