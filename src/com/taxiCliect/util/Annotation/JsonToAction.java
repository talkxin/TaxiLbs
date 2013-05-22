package com.taxiCliect.util.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonToAction {
	/**
	 * 会不会被映射进json
	 * 
	 * @return
	 */
	boolean toJson() default true;
}
