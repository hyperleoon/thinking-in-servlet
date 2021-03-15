package com.hyperleon.research.web.context.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author leon
 * @date 2021-03-15 20:41
 **/
public class LocalConfigSource implements ConfigSource {

    private Properties properties;

    public LocalConfigSource() {
        properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(inputStream);
        } catch (Exception e) {

        }
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String,String> properties = new HashMap<>(this.properties.size());
        for (Map.Entry entry:properties.entrySet()) {
            properties.put((String) entry.getKey(),(String) entry.getValue());
        }
        return properties;
    }

    @Override
    public int getOrdinal() {
        return 200;
    }

    @Override
    public Set<String> getPropertyNames() {
        Set<String> keys = new HashSet<>();
        properties.keySet().forEach(key -> keys.add((String)key));
        return keys;
    }

    @Override
    public String getValue(String propertyName) {
        return properties.getProperty(propertyName);
    }

    @Override
    public String getName() {
        return null;
    }

}
