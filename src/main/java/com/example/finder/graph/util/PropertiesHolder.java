package com.example.finder.graph.util;


import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class PropertiesHolder {
    private Properties properties = new Properties();
    /**
     * 配置文件路径，仅支持classpath下的资源文件加载
     */
    private List<String> propertiesPath = new ArrayList<>();


    @Setter
    @Accessors(chain = true)
    public static class Builder {
        /**
         * 配置文件路径，仅支持classpath下的资源文件加载
         */
        private List<String> propertiesPath = new ArrayList<>();

        public Builder addPropertiesFile(String pattern) {
            propertiesPath.add(pattern);
            return this;
        }

        public Builder addAllPropertiesFile(List<String> patterns) {
            propertiesPath.addAll(patterns);
            return this;
        }

        public PropertiesHolder build() {
            if (propertiesPath.size() == 0) {
                propertiesPath.add("auto-job.yml");
                propertiesPath.add("application.properties");
            }
            PropertiesHolder propertiesHolder = new PropertiesHolder(propertiesPath.toArray(new String[]{}));
            propertiesHolder.propertiesPath = propertiesPath;
            return propertiesHolder;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public void reload() {
        properties = load(propertiesPath.toArray(new String[]{}));
    }


    private PropertiesHolder(String... configFiles) {
        this.properties = load(configFiles);
    }

    private static Properties load(String... configFiles) {
        Properties properties = new Properties();
        for (String location : configFiles) {
            try {
                InputStream is = getClassPathResourceInputStream(location);
                if (is == null) {
                    log.warn("没有找到资源文件：{}", location);
                    continue;
                }
                try {
                    log.info("加载资源文件：{}", location);
                    if (location.endsWith(".properties")) {
                        properties.load(is);
                    } else if (location.endsWith(".yml")) {
                        Map<String, Object> content = YamlParser.classPathYaml2FlattenedMap(location);
                        for (Map.Entry<String, Object> entry : content.entrySet()) {
                            properties.setProperty(entry.getKey(), entry.getValue() + "");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(is);
                }
            } catch (Exception e) {
                log.error("Load " + location + " failure. ", e);
            }
            // 存储当前加载的配置文件路径和名称
            properties.setProperty("configFiles", StringUtils.join(configFiles, ","));

        }
        return properties;
    }

    private static InputStream getClassPathResourceInputStream(String fileName) {
        return PropertiesHolder.class
                .getClassLoader()
                .getResourceAsStream(fileName);
    }

    public Properties getProperties() {
        return properties;
    }

    private static final Pattern p1 = Pattern.compile("\\$\\{.*?\\}");

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            // 支持嵌套取值的问题 key=${xx}/yy
            Matcher m = p1.matcher(value);
            while (m.find()) {
                String g = m.group();
                String keyChild = g
                        .replaceAll("\\$\\{", "")
                        .replaceAll("\\}", "");
                value = value.replace(g, getProperty(keyChild));
            }
            return value;
        } else {
            return System.getProperty(key);
        }
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public <T> T getProperty(String key, Class<T> clazz, String defaultValue) {
        T value = getProperty(key, clazz);
        return value == null ? parseStringValue(defaultValue, clazz) : value;
    }

    public <T> T getProperty(String key, Class<T> clazz) {
        return parseStringValue(properties.getProperty(key, ""), clazz);
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseStringValue(String value, Class<T> type) {
        try {
            if (type == Boolean.class) {
                if (StringUtils.isEmpty(value)) {
                    return null;
                }
                return (T) Boolean.valueOf(value);
            } else if (type == Integer.class) {
                return (T) Integer.valueOf(value);
            } else if (type == Double.class) {
                return (T) Double.valueOf(value);
            } else if (type == Long.class) {
                return (T) Long.valueOf(value);
            } else if (type == List.class) {
                return (T) Arrays.asList(value.split(","));
            } else if (type == String.class) {
                return (T) value;
            } else if (type == BigDecimal.class) {
                return (T) new BigDecimal(value);
            }
        } catch (Exception ignored) {
            return null;
        }
        return JsonUtil.jsonStringToPojo(value, type);
    }


}