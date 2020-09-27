package com.pps.framework.processor;


import com.pps.framework.annotion.*;
import com.pps.framework.bean.BeanDefinetion;
import com.pps.framework.customface.BeanDefinePostRegsterProcessor;
import com.pps.framework.customface.BeanPostProcessor;
import com.pps.framework.customface.FactoryBean;
import com.pps.framework.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author
 * @discription;
 * @time 2020/9/24 11:13
 */
public class ScanProcessor {

    private Class scanClass;

    private List<Class>  beanDefinePostRegsterProcessor=new ArrayList();
    private List<Class>  beanPostProcessor=new ArrayList();
    private List<Class>  aopProxys=new ArrayList<>();

    public List<BeanPostProcessor> getBeanPostProcessor() {

      return  this.beanPostProcessor.stream().map(c->{
          try {
              return (BeanPostProcessor)c.newInstance();
          } catch (InstantiationException e) {
              e.printStackTrace();
          } catch (IllegalAccessException e) {
              e.printStackTrace();
          }
          return  null;
      }).collect(Collectors.toList()) ;
    }

    public List<AopProxy> getAopProxys() {
        return aopProxys.stream().map(c->{
            try {
                return (AopProxy)c.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return  null;
        }).collect(Collectors.toList());
    }

    public ScanProcessor(Class cla, ConcurrentHashMap<String, BeanDefinetion> concurrentSkipListMap) {
        this.scanClass = cla;
        if (scanClass.isAnnotationPresent(PpsScan.class)) {
            PpsScan annotation = (PpsScan) scanClass.getAnnotation(PpsScan.class);
            String basePackage = annotation.basePackage();
            if (basePackage.equals("")) {
                Package aPackage = cla.getPackage();
                basePackage = aPackage.getName();
            }
            Set<Class<?>> classes = ClassUtil.getClasses(basePackage);

            for (Class c : classes) {

                if (c.isAnnotationPresent(PpsCompoent.class)) {

                    registerBeanDefine(c,concurrentSkipListMap);

                }
                if(c.isAnnotationPresent(EnableAspect.class)){
                    EnableAspect annotation1 = (EnableAspect) c.getAnnotation(EnableAspect.class);
                    Class<? extends Annotation> aClass = annotation1.annotationType();
                    boolean ppsImport= aClass.isAnnotationPresent(PpsImport.class);
                            if(ppsImport) {
                                Class[] value = ((PpsImport)aClass.getAnnotation(PpsImport.class)).value();
                                for (Class c2 : value) {
                                    registerBeanDefineFromImport(c2, concurrentSkipListMap);
                                }
                            }
                }

                }

            }

            //beanDefinePost后置处理器处理
            beanDefinePostRegsterProcessor.stream().forEach(s -> {
                try {
                    BeanDefinePostRegsterProcessor beanDefinePostRegsterProcessor = (BeanDefinePostRegsterProcessor) s.newInstance();

                    beanDefinePostRegsterProcessor.postDefineRegister(concurrentSkipListMap);

                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            });

        }


        protected void registerBeanDefine(Class c,ConcurrentHashMap concurrentSkipListMap){
            boolean isAddBeanDefine = true;
            PpsCompoent ppsCompoent= (PpsCompoent) c.getAnnotation(PpsCompoent.class);
            String beanName = ppsCompoent.value();
            BeanDefinetion beanDefinetion=new BeanDefinetion();
            beanDefinetion.setBeanClass(c);
            beanName= beanName.equals("") ? ClassUtil.getBeanName(c.getSimpleName()): beanName;
            beanDefinetion.setBeanName(beanName);
            if(c.isAnnotationPresent(PpsScope.class)){
                PpsScope scope= (PpsScope) c.getAnnotation(PpsScope.class);
                beanDefinetion.setScope(scope.scope());
            }else {
                beanDefinetion.setScope(Scope.Singel);
            }

            if(FactoryBean.class.isAssignableFrom(c)){
                BeanDefinetion beanDefinetion2=new BeanDefinetion();
                try {
                    FactoryBean factoryBean= (FactoryBean) c.newInstance();
                    beanDefinetion2.setBeanClass(factoryBean.getObject().getClass());
                    String beanName1 = factoryBean.getBeanName();
                    beanDefinetion2.setBeanName(beanName1);
                    beanDefinetion2.setScope(factoryBean.getScope());
                    concurrentSkipListMap.put(beanName1,beanDefinetion2);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
            if(BeanDefinePostRegsterProcessor.class.isAssignableFrom(c)){
                beanDefinePostRegsterProcessor.add(c);
                isAddBeanDefine=false;
            }
            if(BeanPostProcessor.class.isAssignableFrom(c)){
                if(c==AopProxy.class){
                    aopProxys.add(c);
                }else {
                    beanPostProcessor.add(c);
                }
                isAddBeanDefine=false;
            }
            if(isAddBeanDefine){
                concurrentSkipListMap.put(beanName, beanDefinetion);
            }


        }

    protected void registerBeanDefineFromImport(Class c,ConcurrentHashMap concurrentSkipListMap){
        boolean isAddBeanDefine = true;

        BeanDefinetion beanDefinetion=new BeanDefinetion();
        beanDefinetion.setBeanClass(c);
        beanDefinetion.setBeanName(ClassUtil.getBeanName(c.getSimpleName()));
        beanDefinetion.setScope(Scope.Singel);


        if(FactoryBean.class.isAssignableFrom(c)){
            BeanDefinetion beanDefinetion2=new BeanDefinetion();
            try {
                FactoryBean factoryBean= (FactoryBean) c.newInstance();
                beanDefinetion2.setBeanClass(factoryBean.getObject().getClass());
                String beanName1 = factoryBean.getBeanName();
                beanDefinetion2.setBeanName(beanName1);
                beanDefinetion2.setScope(factoryBean.getScope());
                concurrentSkipListMap.put(beanName1,beanDefinetion2);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        if(BeanDefinePostRegsterProcessor.class.isAssignableFrom(c)){
            beanDefinePostRegsterProcessor.add(c);
            isAddBeanDefine=false;
        }
        if(BeanPostProcessor.class.isAssignableFrom(c)){
            if(c==AopProxy.class){
                aopProxys.add(c);
            }else {
                beanPostProcessor.add(c);
            }
            isAddBeanDefine=false;
        }
        if(isAddBeanDefine){
            concurrentSkipListMap.put(beanDefinetion.getBeanName(), beanDefinetion);
        }


    }

}
