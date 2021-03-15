package com.hyperleon.research.web.context.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Map;
import java.util.Set;

/**
 * @author leon
 * @date 2021-03-15 20:41
 **/
public class SystemEnvConfigSource implements ConfigSource {

    @Override
    public Map<String, String> getProperties() {
        return System.getenv();
    }

    @Override
    public int getOrdinal() {
        return 100;
    }

    @Override
    public Set<String> getPropertyNames() {
        return null;
    }

    @Override
    public String getValue(String propertyName) {
        return System.getenv(propertyName);
    }

    @Override
    public String getName() {
        return null;
    }
}
