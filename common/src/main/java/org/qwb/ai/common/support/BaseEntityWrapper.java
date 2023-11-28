package org.qwb.ai.common.support;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseEntityWrapper<E, V> {

	/**
	 * 单个实体类包装
	 *
	 * @param entity 实体类
	 * @return V
	 */
	public abstract V entityVO(E entity);

	/**
	 * 实体类集合包装
	 *
	 * @param list 列表
	 * @return List V
	 */
	public List<V> listVO(List<E> list) {
		return list.stream().map(this::entityVO).collect(Collectors.toList());
	}

}