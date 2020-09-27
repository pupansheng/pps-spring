package com.pps.framework.component;


import com.pps.framework.annotion.PpsAspect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 * @discription;
 * @time 2020/9/25 11:18
 */
public class MyHandel implements InvocationHandler {
    private Object trarget;
    private ConcurrentHashMap<String,Object> adviceMap=new ConcurrentHashMap<>();
    private HashMap<String, PpsAspect> aspectMap=new HashMap<>();
    public MyHandel(Object trarget){
        this.trarget=trarget;
        Arrays.stream(trarget.getClass().getDeclaredMethods()).filter(p->p.isAnnotationPresent(PpsAspect.class)).forEach(p->{
            adviceMap.put(p.getName(),p.getAnnotation(PpsAspect.class));
        });

    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        PpsAspect annotation= (PpsAspect) adviceMap.get(method.getName());
        if(annotation!=null){
            Object advice=null;
            Class aClass = annotation.adviceClass();
            Method declaredMethod1 = aClass.getDeclaredMethods()[0];
            String methodName=declaredMethod1.getName();
            if(adviceMap.get(aClass.getName())==null) {
                advice = aClass.newInstance();
                adviceMap.put(aClass.getName(),advice);
            }else {
                advice=adviceMap.get(aClass.getName());
            }
            Method declaredMethod = Arrays.stream(aClass.getDeclaredMethods()).filter(p->p.getName().equals(methodName)).findFirst().get();
            return   declaredMethod.invoke(advice,trarget,method,args);

        }else {
          return   method.invoke(trarget,args);
        }


    }

    public Object wapper(Class c[]){

       return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),c,this);

    }
}
