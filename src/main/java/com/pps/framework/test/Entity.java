package com.pps.framework.test;

import com.pps.framework.annotion.PpsAutoWire;
import com.pps.framework.annotion.PpsCompoent;

/**
 * @author
 * @discription;
 * @time 2020/9/27 11:06
 */
@PpsCompoent
public class Entity {
    @PpsAutoWire
    Say service;

}
