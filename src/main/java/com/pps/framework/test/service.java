package com.pps.framework.test;

import com.pps.framework.annotion.PpsAspect;
import com.pps.framework.annotion.PpsAutoWire;
import com.pps.framework.annotion.PpsCompoent;

/**
 * @author
 * @discription;
 * @time 2020/9/27 11:02
 */
@PpsCompoent
public class service implements Say{

    @PpsAutoWire
    Entity entity;

    @PpsAspect(adviceClass = MyAdvice.class)
    @Override
    public void f1(){
        System.out.println("f1");
    }

}
