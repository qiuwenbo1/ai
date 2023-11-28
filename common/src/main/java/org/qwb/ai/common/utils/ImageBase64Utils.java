/*
 * @Author: liu lichao
 * @Date: 2021-06-30 02:37:06
 * @LastEditors: liu lichao
 * @LastEditTime: 2021-06-30 02:50:29
 * @Description: file content
 */
package org.qwb.ai.common.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import cn.hutool.core.codec.Base64;

public class ImageBase64Utils {
    
    public static String ImageToBase64(String imgPath) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回Base64编码过的字节数组字符串

        return Base64.encode(Objects.requireNonNull(data));
    }

}