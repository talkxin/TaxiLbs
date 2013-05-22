package com.taxiCliect.util.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TableName {
	/**
	 * 表名
	 * 
	 * @return
	 */
	String name();

	/**
	 * 主键
	 * 
	 * @return
	 */
	String tableKey();

	/**
	 * 是否递增
	 * 
	 * @return
	 */
	boolean nullable();
}
