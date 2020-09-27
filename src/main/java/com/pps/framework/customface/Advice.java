package com.pps.framework.customface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author
 * @discription;
 * @time 2020/9/25 15:10
 */
public abstract class Advice {

   protected abstract Object aspect(Object target, Method method, Object... args)  throws InvocationTargetException, IllegalAccessException;

}
