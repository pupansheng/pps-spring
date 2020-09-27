package com.pps.framework;


import com.pps.framework.annotion.PpsAutoWire;
import com.pps.framework.annotion.Scope;
import com.pps.framework.bean.BeanDefinetion;
import com.pps.framework.customface.BeanPostProcessor;
import com.pps.framework.customface.CreateBean;
import com.pps.framework.processor.AopProxy;
import com.pps.framework.processor.ScanProcessor;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author
 * @discription;
 * @time 2020/9/24 11:18
 */
public class PpsContext {

    private ScanProcessor scanProcessor;

    private ConcurrentHashMap<String,Object> singleBeanMap=new ConcurrentHashMap();//一级缓存 存放最终对象

    private ConcurrentHashMap<String,Object> singleEarlyBeanMap=new ConcurrentHashMap();//二级缓存 存放初始化还未完成属性赋值 但是已经被代理了

    private ConcurrentHashMap<String, CreateBean> beanFactory=new ConcurrentHashMap<>();//三级缓存  存放产生类的函数式接口包含代理

    private ConcurrentSkipListSet currentCreateMap=new ConcurrentSkipListSet();

    private ConcurrentHashMap<String, BeanDefinetion> beanDefineTionMap=new ConcurrentHashMap();
    public PpsContext(Class cla){
        //扫描
        scanProcessor=new ScanProcessor(cla,beanDefineTionMap);
        //实例化
        beanDefineTionMap.forEach((k,v)->{
            if(v.getScope()== Scope.Singel){
                createBean(v);
            }
        });
    }
    public ConcurrentHashMap getSingleBeanMap() {
        return singleBeanMap;
    }

    public Object getBean(String name)  {

        BeanDefinetion beanDefinetion = beanDefineTionMap.get(name);
        if(beanDefinetion==null){
            return null;
        }

        Scope scope = beanDefinetion.getScope();
        if(scope==Scope.Prototype){
            try {
                return beanDefinetion.getBeanClass().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Object o = singleBeanMap.get(name);
        if(o==null){

            Object bean = createBean(beanDefinetion);
            return  bean;
        }
        return  o;


    }

    private  Object getSingleBean(String name){

        Object o = singleBeanMap.get(name);
        if(o==null){
            o= singleEarlyBeanMap.get(name);
            if(o==null){
                CreateBean createBean = beanFactory.get(name);
                if(createBean!=null){
                  o=createBean.getBean();
                  singleEarlyBeanMap.put(name,o);
                  beanFactory.remove(name);
                }
            }
        }
        return o;
    }

    private Object wapperObject(Object source,String beanName){

        BeanDefinetion beanDefinetion = beanDefineTionMap.get(beanName);
        if(singleEarlyBeanMap.get(beanDefinetion.getBeanName())==null) {
            for (AopProxy aopProxy : scanProcessor.getAopProxys()) {
                source =aopProxy.afterIntia(beanDefinetion.getBeanName(), source);
            }
        }
        return  source;

    }
    private Object getCreateBean(String beanName){

        Object o = null;
        BeanDefinetion beanDefinetion = beanDefineTionMap.get(beanName);
        try {
            o = beanDefinetion.getBeanClass().newInstance();
            //放入正在制造标记列表
            currentCreateMap.add(beanName);
            // 放入工厂
            Class beanClass = beanDefinetion.getBeanClass();

            try {
                o= beanClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Object finalO = o;
            beanFactory.put(beanDefinetion.getBeanName(),()->{
                return wapperObject(finalO,beanName);
            });

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return  o;

    }

    private Object createBean(BeanDefinetion beanDefinetion){



        Object singleBean1 = getSingleBean(beanDefinetion.getBeanName());
        if(singleBean1!=null){
            return singleBean1;
        }
        //获得原始对象 并将函数时存入factory
        Object singleBean =getCreateBean(beanDefinetion.getBeanName());
        //---------------为属性自动赋值
       Class beanClass= beanDefinetion.getBeanClass();
        Field[] fields = beanClass.getDeclaredFields();
        for(Field f: fields){
            if(f.isAnnotationPresent(PpsAutoWire.class)){
                f.setAccessible(true);
                Object f2 = getSingleBean(f.getName());
                if(f2==null){//依赖还没建立
                        if(currentCreateMap.contains(f.getName())&&beanDefinetion.getScope()==Scope.Prototype){
                            throw  new RuntimeException("多例出现循环依赖 无法解决----");
                        }
                        f2=createBean(beanDefineTionMap.get(f.getName()));
                 }
                try {
                    f.set(singleBean,f2);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }

        //如果没有提前aop  进行类的增强
        boolean isHasPre=false;
        isHasPre=singleEarlyBeanMap.get(beanDefinetion.getBeanName())==null;
        if(isHasPre) {
            for (AopProxy aopProxy : scanProcessor.getAopProxys()) {
                singleBean =aopProxy.afterIntia(beanDefinetion.getBeanName(), singleBean);
            }
        }

        //类初始化后置处理器
        if(singleEarlyBeanMap.get(beanDefinetion.getBeanName())==null) {
            for (BeanPostProcessor beanPostProcessor : scanProcessor.getBeanPostProcessor()) {
                singleBean = beanPostProcessor.afterIntia(beanDefinetion.getBeanName(), singleBean);
            }
        }
        //单例存进单例池
        if(beanDefinetion.getScope()==Scope.Singel) {

            Object t=  singleEarlyBeanMap.remove(beanDefinetion.getBeanName());//若能从二级缓存取到值  则说明 已经被代理过了  就不能存取原对象了
            singleBeanMap.put(beanDefinetion.getBeanName(),t ==null?singleBean:t);



        }


        currentCreateMap.remove(beanDefinetion.getBeanName());
        return  singleBean;
    }
}
