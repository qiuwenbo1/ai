package org.qwb.ai.common.support;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.qwb.ai.common.utils.Kv;

import java.util.List;
import java.util.Map;

/**
 * @author demoQ
 * @date 2022/1/13 14:47
 */
public class CommonCondition {

    public CommonCondition() {
    }

    public static <T> CommonSpecification<T> getQuerySpecification(Map<String, Object> query, Class<T> clazz) {
        Kv exclude = Kv.init().set("current", "current").set("size", "size").set("ascs", "ascs").set("descs", "descs").set("page", "page");
        return getQuerySpecification(query, exclude, clazz);
    }

    public static <T> CommonSpecification<T> getQuerySpecification(Map<String, Object> query, Map<String, Object> exclude, Class<T> clazz) {
        exclude.forEach((k, v) -> {
            query.remove(k);
        });
        if (!query.containsKey("isDeleted")) {
            query.put("isDeleted", "正常");
        }

        if (!query.containsKey("status")) {
            query.put("status", "正常");
        }
        CommonSpecification<T> qs = new CommonSpecification<>();
        query.forEach((k, v) -> {
            if (k.equals("id")) {
                if (v instanceof List) {
                    qs.add(new SearchCriteria(k, v, SearchOperation.IN));
                } else {
                    qs.add(new SearchCriteria(k, Convert.toLong(v), SearchOperation.EQUAL));
                }
            } else if (ReflectUtil.hasField(clazz, k)) {
                if (v instanceof String) {
                    if (StrUtil.endWith(k, "Id", true)) {
                        qs.add(new SearchCriteria(k, Convert.toLong(v), SearchOperation.EQUAL));
                    } else if (StrUtil.equals(String.valueOf(v), "true", true)) {
                        qs.add(new SearchCriteria(k, v, SearchOperation.TRUE));
                    } else if (StrUtil.equals(String.valueOf(v), "false", true)) {
                        qs.add(new SearchCriteria(k, v, SearchOperation.FALSE));
                    } else {
                        qs.add(new SearchCriteria(k, v, SearchOperation.MATCH));
                    }
                } else if (v instanceof Long) {
                    qs.add(new SearchCriteria(k, v, SearchOperation.EQUAL));
                } else if (v instanceof List) {
                    if (ObjectUtil.isEmpty(v)) return;
                    qs.add(new SearchCriteria(k, v, SearchOperation.IN));
                } else {
                    qs.add(new SearchCriteria(k, v, SearchOperation.EQUAL));
                }
            }
        });
        return qs;
    }
}
