/**
 * @Auther: Don
 * @Date: 2019/9/5 14:24
 * @Description:
 */

import java.util.Arrays;

public class testArrayDifference {
    public static void main(String[] args) {
        StrToBinstr("12123424");
        StrToBinstr("12123425");

    }

    // 将字符串转换成二进制字符串，以空格相隔
    private static String StrToBinstr(String str) {
        char[] strChar = str.toCharArray();
        String result = "";
        for (int i = 0; i < strChar.length; i++) {
            result += Integer.toBinaryString(strChar[i]) + " ";
        }
        System.out.println(result);
        return result;
    }
}


