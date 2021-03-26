package com.trychen.simplerpc.annotation;

import com.trychen.simplerpc.framework.persistence.JsonPersistence;
import com.trychen.simplerpc.framework.persistence.Persistence;

import javax.annotation.Generated;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RPC {
    String value();

    boolean fast() default false;

    Class<? extends Persistence> persistence() default JsonPersistence.class;

    boolean receiveLocal() default true;
}
