package com.pingan.robot.calc.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.utility.Predefine;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/11 14:35
 **/
public class HanLPConfig {
    public static Properties p = new Properties();

    static {
        // 自动读取配置
        p = new Properties();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                loader = HanLP.Config.class.getClassLoader();
            }
            try {
                p.load(new InputStreamReader(Predefine.HANLP_PROPERTIES_PATH == null ?
                        loader.getResourceAsStream("hanlp.properties") :
                        new FileInputStream(Predefine.HANLP_PROPERTIES_PATH)
                        , "UTF-8"));
            } catch (Exception e) {
                ;
            }
        } catch (Exception e) {
            ;
        }
    }

    public static String getConfig(String config) {
        return p.getProperty(config, "");
    }

}
