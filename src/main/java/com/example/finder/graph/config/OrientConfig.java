package com.example.finder.graph.config;

import com.example.finder.graph.util.PropertiesHolder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * orientDB相关配置
 *
 * @Author Huang Yongxiang
 * @Date 2022/08/30 10:20
 */
@Getter
public class OrientConfig {
    /**
     * 数据库URL
     */
    private final String url;
    /**
     * 数据库名
     */
    private final String database;
    /**
     * 用户名
     */
    private final String username;
    /**
     * 密码
     */
    private final String password;
    /**
     * 会话池最小会话数目
     */
    private final Integer minPoolSize;
    /**
     * 会话池最大会话数目
     */
    private final Integer maxPoolSize;
    /**
     * 会话池会话获取最大超时时长：秒
     */
    private final Integer acquireTimeout;

    private OrientConfig() {
        PropertiesHolder propertiesHolder = PropertiesHolder
                .builder()
                .addPropertiesFile("orientdb-config.yml")
                .build();
        url = propertiesHolder.getProperty("orientDB.url", "");
        database = propertiesHolder.getProperty("orientDB.database", "");
        username = propertiesHolder.getProperty("orientDB.username", "root");
        password = propertiesHolder.getProperty("orientDB.password", "0000");
        minPoolSize = propertiesHolder.getProperty("orientDB.pool", Integer.class, "5");
        maxPoolSize = propertiesHolder.getProperty("orientDB.pool.min", Integer.class, "10");
        acquireTimeout = propertiesHolder.getProperty("orientDB.pool.acquireTimeout", Integer.class, "30");
    }

    public static OrientConfig getInstance() {
        return InstanceHolder.ORIENT_CONFIG;
    }

    static class InstanceHolder {
        private static final OrientConfig ORIENT_CONFIG = new OrientConfig();
    }
}
