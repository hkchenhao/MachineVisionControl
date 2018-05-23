package com.hanyu.hust.testnet.utils;

/**
 * Created by xinshangqiu on 2017/7/17.
 */

public class AlgSettingUtils {

    public static int findIndex(String[] arrays, String val) {
        int index = 0;
        for (String array : arrays) {
            if (array.equals(val))
                return index;
            index++;
        }
        if (index == arrays.length)
            return 0;
        return index;
    }

}
