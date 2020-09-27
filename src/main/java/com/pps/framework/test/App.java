package com.pps.framework.test;

import com.pps.framework.PpsContext;
import com.pps.framework.annotion.EnableAspect;
import com.pps.framework.annotion.PpsScan;

/**
 * @author
 * @discription;
 * @time 2020/9/27 11:13
 */
@PpsScan
@EnableAspect
public class App {

    public static void  main(String args[]){

         PpsContext ppsContext=new PpsContext(App.class);
         Say service= (Say) ppsContext.getBean("service");
         service.f1();


    }

}
