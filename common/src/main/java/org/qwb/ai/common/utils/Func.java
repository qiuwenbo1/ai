package org.qwb.ai.common.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;


public class Func {

	public static JSONObject merge(JSONObject... jsonObjects){
		JSONObject obj = JSONUtil.createObj();

		for(JSONObject temp: jsonObjects) {
			for(Map.Entry entry: temp.entrySet()){
				String key = entry.getKey().toString();
				obj.set(key, entry.getValue());
			}
		}
		return obj;
	}

    /**
	 * 转换为Long集合<br>
	 *
	 * @param str 结果被转换的值
	 * @return 结果
	 */
	public static List<Long> toLongList(String str) {
		return Arrays.asList(toLongArray(str));
    }
    
    /**
	 * 转换为Long数组<br>
	 *
	 * @param str 被转换的值
	 * @return 结果
	 */
	public static Long[] toLongArray(String str) {
		return toLongArray(",", str);
    }
    
    /**
	 * 转换为Long数组<br>
	 *
	 * @param split 分隔符
	 * @param str   被转换的值
	 * @return 结果
	 */
	public static Long[] toLongArray(String split, String str) {
		if (StrUtil.isEmpty(str)) {
			return new Long[]{};
		}
		String[] arr = str.split(split);
		final Long[] longs = new Long[arr.length];
		for (int i = 0; i < arr.length; i++) {
			final Long v = toLong(arr[i], 0);
			longs[i] = v;
		}
		return longs;
    }
    
    /**
	 * <p>Convert a <code>String</code> to a <code>long</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   $.toLong(null, 1L) = 1L
	 *   $.toLong("", 1L)   = 1L
	 *   $.toLong("1", 0L)  = 1L
	 * </pre>
	 *
	 * @param value        the string to convert, may be null
	 * @param defaultValue the default value
	 * @return the long represented by the string, or the default if conversion fails
	 */
	public static long toLong(final Object value, final long defaultValue) {
		return Convert.toLong(String.valueOf(value), defaultValue);
	}
}