package com.pps.framework.bean;


import com.pps.framework.annotion.Scope;

/**
 * @author
 * @discription;
 * @time 2020/9/24 14:14
 */
public class BeanDefinetion {
    private Class beanClass;
    private Scope scope;
    private String beanName;

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
