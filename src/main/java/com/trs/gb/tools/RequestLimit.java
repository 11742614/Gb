package com.trs.gb.tools;

/**
 * Created by 苏亚青 on 2019/1/13.
 */
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import  java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import  java.lang.annotation.Retention;
import  java.lang.annotation.RetentionPolicy;
import  java.lang.annotation.Target;
import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
//最高优先级
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit{
        /**
          * 
          * 允许访问的次数，默认值MAX_VALUE
          */
        int count() default Integer.MAX_VALUE;

    /**
         * 
         * 时间段，单位为秒，默认值一分钟
         */
        int time() default 60;
    }

