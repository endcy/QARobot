package com.utils;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/13 16:04
 **/
public class PrepertiesGet {
    @Test
    public void getUserDir() {
        System.out.println(System.getProperty("user.dir"));
        ;
    }

    @Test
    public void getFloatRate() {
        float rate = 0l;
        rate = 1 / 22;
        System.out.println(rate);
        rate = (float) 1 / (float) 22;
        System.out.println(rate);
        float avgRate = 129.13338f;
        System.out.println(avgRate/156);
    }

    @Test
    public void repAllblank(){
        String content = "我是  你猜    \r      什么呀？";
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(content);
        content = m.replaceAll("");
        System.out.println(content);
    }
}
