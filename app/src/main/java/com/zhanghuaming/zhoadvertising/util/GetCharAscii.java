package com.zhanghuaming.zhoadvertising.util;

/**
 * Created by zhang on 2018/2/24.
 */

public class GetCharAscii {

    /*0-9对应Ascii 48-57
     *A-Z 65-90
     *a-z 97-122
     *第33～126号(共94个)是字符，其中第48～57号为0～9十个阿拉伯数字
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        System.out.println(charToByteAscii('\n'));//ascii字符转10进制
        System.out.println(byteAsciiToChar(57));//10进制转ascii字符
        System.out.println(SumStrAscii("19"));
        System.out.println(SumStrAscii("一"));
    }


    public static byte[] stringEN2Ascii(String str){
        char[] chars = str.toCharArray();
        byte[] result = new byte[chars.length];
        int length = chars.length;
        for(int i=0;i<length;i++){
            result[i] = charToByteAscii(chars[i]);
        }
        return result;
    }

    public static String Ascii2StringEN(byte[] bytes,int index,int ret){
        StringBuilder result = new StringBuilder();
        for (int i=index;i<index+ret;i++){
            result.append(byteAsciiToChar(bytes[i]));
        }
        return result.toString();
    }
    /**
     * 方法一：将char 强制转换为byte
     * @param ch
     * @return
     */
    public static byte charToByteAscii(char ch){
        byte byteAscii = (byte)ch;

        return byteAscii;
    }
    /**
     * 方法二：将char直接转化为int，其值就是字符的ascii
     * @param ch
     * @return
     */
    public static byte charToByteAscii2(char ch){
        byte byteAscii = (byte)ch;

        return byteAscii;
    }
    /**
     * 同理，ascii转换为char 直接int强制转换为char
     * @param ascii
     * @return
     */
    public static char byteAsciiToChar(int ascii){
        char ch = (char)ascii;
        return ch;
    }
    /**
     * 求出字符串的ASCII值和
     * 注意，如果有中文的话，会把一个汉字用两个byte来表示，其值是负数
     */
    public static int SumStrAscii(String str){
        byte[] bytestr = str.getBytes();
        int sum = 0;
        for(int i=0;i<bytestr.length;i++){
            sum += bytestr[i];
        }
        return sum;
    }
}
