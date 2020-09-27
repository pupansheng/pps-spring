package com.pps.framework.customface;


import com.pps.framework.annotion.Scope;

public interface FactoryBean {

    Object getObject();
    String getBeanName();
    default Scope getScope(){

        return Scope.Singel;
    }

}
