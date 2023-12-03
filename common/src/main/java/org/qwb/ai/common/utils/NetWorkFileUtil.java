package org.qwb.ai.common.utils;

import cn.hutool.core.util.IdUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * @author demoQ
 * @date 2022/1/27 15:30
 */
public class NetWorkFileUtil {

    /**
     * 获取网络图片保存到本地
     *
     * @param imageUrl 图片链接
     * @return 保存的文件
     */
    public static File urlToFile(String imageUrl) {
        URL url;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        File file = null;
        FileOutputStream fout = null;
        try {
            url = new URI(imageUrl).toURL();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

            baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            file = File.createTempFile(IdUtil.simpleUUID(), ".jpg");
            fout = new FileOutputStream(file);
            baos.writeTo(fout);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return file;
    }

    /**
     * 获取网络图片的输入流
     *
     * @param imageUrl 图片链接
     * @return 输入流
     */
    public static InputStream urlToInputStream(String imageUrl) {
        URL url;
        HttpURLConnection urlConnection;
        InputStream inputStream = null;
        try {
            url = new URL(imageUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}
