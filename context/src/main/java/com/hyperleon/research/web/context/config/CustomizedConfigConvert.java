package com.hyperleon.research.web.context.config;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author leon
 * @date 2021-03-15 20:47
 **/
public class CustomizedConfigConvert<T> implements Converter<T> {

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        return (T)"customized converter take over it ^^";
    }
}
