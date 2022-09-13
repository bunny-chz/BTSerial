/**                     
    * Project:  BTSerial
    * Comments: 工具类，对单片机传来的数据进行类型转换
    * JDK version used: <JDK1.8>
    * Author： Bunny     Github: https://github.com/bunny-chz/
    * Create Date：2022-05-11
    * Version: 1.0
    */

package com.bunny.BTSerial;

/**
 *此类为工具类，用于处理单片机发送来的数据
 */

public class Tools {

    //转化成16进制字符串
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }
        return stringBuilder.toString().toUpperCase();
    }
    //转化成字符串
    public static String bytesToString(byte[] b) {
        StringBuffer result = new StringBuffer("");
        int length = b.length;
        for (int i=0; i<length; i++) {
            result.append((char)(b[i]));
        }
        return result.toString();
    }

}
