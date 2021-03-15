package com.hyperleon.research.web.context.config;

/**
 * @author leon
 * @date 2021-03-15 23:01
 **/
public class ConfigTest {

    public static void main(String[] args) {
        DefaultFacedConfig defaultFacedConfig = new DefaultFacedConfig();
        String appName = defaultFacedConfig.getValue("application.name", String.class);
        Integer integerValue = defaultFacedConfig.getValue("integer.value",Integer.class);
        Double doubleValue = defaultFacedConfig.getValue("double.value",Double.class);
        Object customizeValue = defaultFacedConfig.getValue("customize.value",Object.class);
        System.out.println(appName);
        System.out.println(integerValue);
        System.out.println(doubleValue);
        System.out.println(customizeValue);
    }
}
