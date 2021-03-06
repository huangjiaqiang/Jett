package com.m.objectss.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Project Name: Jett
 * File Name:    KryoField.java
 * ClassName:    KryoField
 * <p>
 * 用于标记需要系列化存储的类
 *
 * @author haungjiaqiang
 * @date 2020年08月11日 下午3:22
 * <p>
 *
 */
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyClass
{
    String key() default "";
}
