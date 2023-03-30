package com.example.finder.graph.framework;

import com.example.finder.graph.factory.OrientSessionFactory;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import lombok.extern.slf4j.Slf4j;

/**
 * OrientDB会话事务管理器
 *
 * @Author Huang Yongxiang
 * @Date 2022/10/08 14:40
 */
@Slf4j
public class TransactionManager {
    private static final ThreadLocal<Boolean> isOpenTransaction = new ThreadLocal<>();
    private static final ThreadLocal<ODatabaseSession> currentSession = new ThreadLocal<>();

    @FunctionalInterface
    public interface TransactionFunction {
        boolean todo(ODatabaseSession session) throws Exception;
    }

    public static void openTransaction() {
        if (isOpenTransaction.get() != null && isOpenTransaction.get()) {
            return;
        }
        isOpenTransaction.set(true);
        ODatabaseSession session = OrientSessionFactory
                .getInstance()
                .getSession();
        session.begin();
        log.debug("事务开启");
        currentSession.set(session);
    }

    public static void closeTransaction() {
        isOpenTransaction.set(false);
        if (currentSession.get() != null) {
            try {
                currentSession
                        .get()
                        .commit()
                        .close();
                currentSession.remove();
                log.debug("事务提交");
            } catch (Exception ignored) {
            }
        }
    }

    public static void rollbackTransaction() {
        isOpenTransaction.set(false);
        if (currentSession.get() != null) {
            try {
                currentSession
                        .get()
                        .rollback()
                        .close();
                currentSession.remove();
                log.debug("事务回滚");
            } catch (Exception ignored) {
            }
        }
    }

    @SafeVarargs
    public static boolean doTransaction(TransactionFunction function, Class<? extends Exception>... type) {
        if (function == null) {
            return false;
        }
        try {
            openTransaction();
            boolean flag = function.todo(getCurrentSession());
            closeTransaction();
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            if (type != null && type.length > 0) {
                for (Class<? extends Exception> ex : type) {
                    if (e.getClass() == ex) {
                        rollbackTransaction();
                        break;
                    }
                }
            } else {
                rollbackTransaction();
            }
        }
        return false;
    }

    public static boolean isOpenTransaction() {
        return isOpenTransaction.get() != null && isOpenTransaction.get();
    }

    /**
     * 获取当前线程绑定的会话实例
     *
     * @return com.orientechnologies.orient.core.db.ODatabaseSession
     * @author Huang Yongxiang
     * @date 2022/10/8 15:31
     */
    public static ODatabaseSession getCurrentSession() {
        return currentSession.get();
    }
}
