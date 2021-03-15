package com.hyperleon.research.web.context.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author leon
 * @date 2021-03-15 20:23
 **/
public class DefaultConfigProviderResolver extends ConfigProviderResolver {

    protected DefaultConfigProviderResolver() {
        super();
    }

    @Override
    public Config getConfig() {
        return getConfig(null);
    }

    @Override
    public Config getConfig(ClassLoader loader) {
        ClassLoader classLoader = loader;
        if (loader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        ServiceLoader<Config> serviceLoader = ServiceLoader.load(Config.class,classLoader);
        Iterator<Config> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        throw new IllegalStateException("No Config implementation found!");
    }

    @Override
    public ConfigBuilder getBuilder() {
        return null;
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {

    }

    @Override
    public void releaseConfig(Config config) {

    }
}
