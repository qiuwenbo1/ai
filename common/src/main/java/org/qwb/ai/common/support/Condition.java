/*
 * @Author: liu lichao
 * @Date: 2020-11-18 23:56:42
 * @LastEditors: liu lichao
 * @LastEditTime: 2021-12-17 15:22:57
 * @Description: 前端传入参数的条件查询
 */
package org.qwb.ai.common.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import org.qwb.ai.common.utils.Kv;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import lombok.SneakyThrows;

public class Condition {

    /**
     * 前端查询参数
     * 过滤掉分页等
     * @param <T> 类型
     * @param query 查询条件
     * @param clazz 实体类
     * @return
     */
    public static <T> GenericSpecification<T> getQuerySpesification(Map<String, Object> query, Class<T> clazz){
        Kv exclude = Kv.init().set("current", "current").set("size", "size").set("ascs", "ascs").set("descs", "descs").set("page", "page");
        return getQuerySpesification(query, exclude, clazz);
    }

    public static <T> GenericSpecification<T> getQuerySpesification(Map<String, Object> query, Map<String, Object> exclude, Class<T> clazz) {
		exclude.forEach((k, v) -> query.remove(k));
        GenericSpecification<T> qs = new GenericSpecification<>();
        Map<String, Object> map = new HashMap<>();
		query.forEach((k, v) -> {
            if(k.equals("id")){
                qs.add(new SearchCriteria(k, Convert.toLong(v), SearchOperation.EQUAL));
            }
            else if(StrUtil.isBlankOrUndefined(Convert.toStr(v))) {

            }
            else{
                if(ReflectUtil.hasField(clazz, k)){
                    if(v instanceof String){
                        qs.add(new SearchCriteria(k, v, SearchOperation.MATCH));
                    }else{
                        qs.add(new SearchCriteria(k, v, SearchOperation.EQUAL));
                    }
                    map.put(k, v);
                }
                // if(objectContainField(clazz, k)){
                    
                // };
            }
        });
        return qs;
    }
    
    @SneakyThrows
    private static boolean objectContainField(Class clazz, String fieldName) {
        // 包含父类
        List<Field> fieldList = new ArrayList<>();
        List<String> fs = new ArrayList<>();
        while (clazz != null){
            for(Field f: clazz.getDeclaredFields()){
                fs.add(f.getName());
            }
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fs.contains(fieldName);
    }

    public static <T> Example<T> getQueryExample(Map<String, Object> query, Class<T> clazz) {
        Kv exclude = Kv.init().set("current", "current").set("size", "size").set("ascs", "ascs").set("descs", "descs")
                .set("page", "page");
        return getQueryExample(query, exclude, clazz);
    }

    public static <T> Example<T> getQueryExample(Map<String, Object> query, Map<String, Object> exclude,
            Class<T> clazz) {
        exclude.forEach((k, v) -> query.remove(k));
        Map<String, Object> map = new HashMap<>();
        query.forEach((k, v) -> {
            if (ReflectUtil.hasField(clazz, k)) {
                map.put(k, v);
            }
        });
        if (map.size() == 0) {
            return null;
        }
        System.out.println(map);
        ExampleMatcher matcher = getMatcher(map);
        return Example.of(BeanUtil.toBeanIgnoreError(query, clazz), matcher);
    }

    public static ExampleMatcher getMatcher(Map<String, Object> query) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnoreCase()
                .withStringMatcher(StringMatcher.CONTAINING).withMatcher("classification", c -> c.contains()).withMatcher("assistLabel", c -> c.contains());
        return matcher;
    }

}