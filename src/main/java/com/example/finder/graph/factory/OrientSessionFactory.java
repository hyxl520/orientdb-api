package com.example.finder.graph.factory;


import com.example.finder.graph.config.OrientConfig;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.*;

import lombok.extern.slf4j.Slf4j;

/**
 * Orient 会话工厂
 *
 * @Author Huang Yongxiang
 * @Date 2022/08/30 10:16
 */
@Slf4j
public class OrientSessionFactory {
    private final OrientConfig config;
    private final OrientDB orient;
    private ODatabaseSession globalSession;
    private final ODatabasePool pool;

    public OrientSessionFactory(OrientConfig config) {
        log.info("开始连接远程OrientDB服务器：url:{}", config.getUrl());
        try {
            this.orient = new OrientDB(String.format("%s:%s", "remote", config.getUrl()), OrientDBConfig.defaultConfig());
            OrientDBConfigBuilder poolCfg = OrientDBConfig.builder();
            poolCfg.addConfig(OGlobalConfiguration.DB_POOL_ACQUIRE_TIMEOUT, config.getAcquireTimeout() * 1000);
            poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MAX, config.getMaxPoolSize());
            poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MIN, config.getMinPoolSize());
            this.pool = new ODatabasePool(orient, config.getDatabase(), config.getUsername(), config.getPassword(), poolCfg.build());
            this.globalSession = orient.open(config.getDatabase(), config.getUsername(), config.getPassword());
            this.config = config;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("连接远程OrientDB服务器{}发生异常：{}:{}", config.getUrl(), e
                    .getClass()
                    .getName(), e.getMessage());
            throw e;
        }

    }

    /**
     * 注意：全局会话不是线程安全的，如果并发执行请优先使用newSession或getSession创建一个新的实例
     *
     * @return com.orientechnologies.orient.core.db.ODatabaseSession
     * @author Huang Yongxiang
     * @date 2022/8/30 11:54
     */
    public ODatabaseSession getGlobalSession() {
        if (globalSession.isClosed()) {
            globalSession = orient.open(config.getDatabase(), config.getUsername(), config.getPassword());
        }
        return boundThread(globalSession);
    }

    /**
     * 从会话池中获取一个会话，会话的关闭由调用者来实现
     *
     * @return com.orientechnologies.orient.core.db.ODatabaseSession
     * @author Huang Yongxiang
     * @date 2022/8/30 12:06
     */
    public ODatabaseSession getSession() {
        return boundThread(pool.acquire());
    }


    /**
     * 创建一个新的会话
     *
     * @return com.orientechnologies.orient.core.db.ODatabaseSession
     * @author Huang Yongxiang
     * @date 2022/8/30 12:07
     */
    public ODatabaseSession newSession() {
        return boundThread(orient.open(config.getDatabase(), config.getUsername(), config.getPassword()));
    }

    public OrientDB getOrientDB() {
        return orient;
    }

    /**
     * 从Spring容器中获取一个实例
     *
     * @return com.example.demo.graph.factory.OrientSessionFactory
     * @author Huang Yongxiang
     * @date 2022/8/30 12:08
     */
    public static OrientSessionFactory getInstance() {
        return InstanceHolder.FACTORY;
    }

    private <T extends ODatabaseSession> T boundThread(T session) {
        if (session instanceof ODatabaseDocumentInternal) {
            ODatabaseRecordThreadLocal
                    .instance()
                    .set((ODatabaseDocumentInternal) session);
        }
        session.activateOnCurrentThread();
        return session;
    }

    static class InstanceHolder {
        private static final OrientSessionFactory FACTORY = new OrientSessionFactory(OrientConfig.getInstance());
    }
}
