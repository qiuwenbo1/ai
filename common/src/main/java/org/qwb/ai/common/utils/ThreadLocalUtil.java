/*
 * @Author: liu lichao
 * @Date: 2021-12-17 10:50:50
 * @LastEditors: liu lichao
 * @LastEditTime: 2021-12-17 10:53:19
 * @Description: file content
 */
package org.qwb.ai.common.utils;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalUtil {

    //使用InheritableThreadLocal，使得共享变量可被子线程继承
    private static final InheritableThreadLocal<Map<String,String>> headerMap = new InheritableThreadLocal<>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    public static Map<String,String> get(){
        return headerMap.get();
    }

    public static String get(String key) {
        return headerMap.get().get(key);
    }

    public static void set(String key, String value){
        headerMap.get().put(key,value);
    }

    public static void remove(){
        headerMap.remove();
    }
    
}

