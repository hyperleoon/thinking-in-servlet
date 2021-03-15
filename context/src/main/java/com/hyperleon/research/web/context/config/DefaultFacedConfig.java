package com.hyperleon.research.web.context.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * @author leon
 * @date 2021-03-15 20:27
 **/
public class DefaultFacedConfig implements Config {

    private List<ConfigSource> configSources = new LinkedList<>();

    private static final List<Converter> customizeConverts = new ArrayList<>();

    private static final Map<Class, Function> baseTypeHandleStrategy = new HashMap<>();

    private Comparator<ConfigSource> configSourceComparator = (o1, o2) -> Integer.compare(o2.getOrdinal(),o1.getOrdinal());

    private final CustomizeCovertChain customizeCovertChain;

    static {
        baseTypeHandleStrategy.put(String.class, Object::toString);
        baseTypeHandleStrategy.put(Integer.class, o -> Integer.valueOf((String)o));
        baseTypeHandleStrategy.put(Double.class, o -> Double.valueOf((String)o));
        baseTypeHandleStrategy.put(Float.class, o -> Float.valueOf((String)o));
    }

    public DefaultFacedConfig() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        ServiceLoader<ConfigSource> configSourcesLoader = ServiceLoader.load(ConfigSource.class,classLoader);
        configSourcesLoader.forEach(configSources::add);
        configSources.sort(configSourceComparator);
        ServiceLoader<Converter> convertLoader = ServiceLoader.load(Converter.class,classLoader);
        convertLoader.forEach(customizeConverts::add);
        this.customizeCovertChain = new CustomizeCovertChain();
    }

    @Override
    public <T> List<T> getValues(String propertyName, Class<T> propertyType) {
        return null;
    }

    @Override
    public <T> Optional<List<T>> getOptionalValues(String propertyName, Class<T> propertyType) {
        return Optional.empty();
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        // String 转换成目标类型
        Optional<Converter> optionalConvert = getConverter(propertyType);
        return (T)optionalConvert.map(e -> e.convert(propertyValue)).orElseGet(() -> customizeCovertChain.convert(propertyValue));
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        return null;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return null;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return Collections.unmodifiableList(configSources);
    }

    @Override
    public <T> Optional getConverter(Class<T> forType) {
        Converter target  = null;
        if (baseTypeHandleStrategy.containsKey(forType)) {
            target = (Converter<T>) value -> (T)baseTypeHandleStrategy.get(forType).apply(value);
        }
        return Optional.ofNullable(target);
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }

    private String getPropertyValue(String propertyName) {
        String value = null;
        for (ConfigSource configSource:configSources) {
            value = configSource.getValue(propertyName);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    public class CustomizeCovertChain<T> implements Converter<T> {
        @Override
        public T convert(String value) {
            for (Converter customizeConvert:customizeConverts) {
                try {
                    return  (T)customizeConvert.convert(value);
                } catch (Throwable throwable) {
                    //continue;
                }
            }
            throw new RuntimeException("can not convert");
        }
    }
}
