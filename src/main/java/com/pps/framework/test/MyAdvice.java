package com.pps.framework.test;

import com.pps.framework.annotion.PpsCompoent;
import com.pps.framework.customface.Advice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author
 * @discription;
 * @time 2020/9/27 11:05
 */
public class MyAdvice extends Advice {
    @Override
    public Object aspect(Object target, Method method, Object... args) throws InvocationTargetException, IllegalAccessException {

        System.out.println("代理------");

        return method.invoke(target,args);
    }
}
