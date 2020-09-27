package com.pps.framework.processor;



import com.pps.framework.annotion.PpsAspect;
import com.pps.framework.annotion.PpsCompoent;
import com.pps.framework.component.MyHandel;
import com.pps.framework.customface.BeanPostProcessor;

import java.lang.reflect.Method;

/**
 * @author
 * @discription;
 * @time 2020/9/25 11:03
 */
public class AopProxy implements BeanPostProcessor {
    @Override
    public Object afterIntia(String name, Object bean) {
        Class<?> aClass = bean.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        boolean isAop=false;
        for(Method method:declaredMethods){
            if(method.isAnnotationPresent(PpsAspect.class)){
                isAop=true;
                break;
            }
        }
        if(isAop) {

            Class<?>[] superclass =  aClass.getInterfaces();
            if(superclass.length>0) {
                System.out.println("对" + name + "类  "  + "进行增强-----");
                    return new MyHandel(bean).wapper(superclass);
            }else {
                throw  new RuntimeException(name+"类"+"无法增强  支持代理接口");
            }
        }
        return bean;
    }
}
