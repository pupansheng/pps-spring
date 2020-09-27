package com.pps.framework.annotion;

public enum Scope {

    Singel(1,"单例"),Prototype(2,"多例");
    private int type;
    private String mes;

     Scope(int t,String s){
        this.type=t;
        this.mes=s;
    }


}
