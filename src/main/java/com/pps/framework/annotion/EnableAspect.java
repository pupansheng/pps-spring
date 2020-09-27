package com.pps.framework.annotion;

import com.pps.framework.processor.AopProxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@PpsImport(AopProxy.class)
public @interface EnableAspect {
}
