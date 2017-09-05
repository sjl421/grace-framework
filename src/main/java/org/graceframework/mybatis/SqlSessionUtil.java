package org.graceframework.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;

import java.sql.SQLException;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class SqlSessionUtil {

    /**
     * 保存当前线程的SqlSession引用
     */
    private static ThreadLocal<SqlSession> sqlSessionHolder = new ThreadLocal<>();

    /**
     * 获取SqlSession - 如果不存在则创建一个
     * @param isolationLevel 隔离级别
     * @param autoCommit 是否开启事务 false 开启事务
     * @param readOnly 是否只读
     * @return SqlSession
     */
    public static SqlSession openSqlSession(int isolationLevel, boolean autoCommit, boolean readOnly) throws SQLException {

        SqlSession sqlSession = sqlSessionHolder.get();
        if (sqlSession == null) {
            SqlSessionFactory sqlSessionFactory = MybatisContext.getSqlSessionFactory();

            if (isolationLevel > 0 && !autoCommit) {
                sqlSession = sqlSessionFactory.openSession(getTransactionIsolationLevel(isolationLevel));
            } else {
                sqlSession = sqlSessionFactory.openSession(autoCommit);
            }

            sqlSession.getConnection().setReadOnly(readOnly);
            sqlSessionHolder.set(sqlSession);
        }
        return sqlSession;
    }

    /**
     * 获取事务隔离级别
     * @param isolationLevel 事务隔离级别
     * @return TransactionIsolationLevel
     */
    private static TransactionIsolationLevel getTransactionIsolationLevel(int isolationLevel) {

        switch (isolationLevel) {
            case 1:
                return  TransactionIsolationLevel.READ_UNCOMMITTED;
            case 2:
                return  TransactionIsolationLevel.READ_COMMITTED;
            case 4:
                return  TransactionIsolationLevel.REPEATABLE_READ;
            case 8:
                return  TransactionIsolationLevel.SERIALIZABLE;
            default:
                return  TransactionIsolationLevel.NONE;
        }
    }

    /**
     * 从当前线程中获取SqlSession
     * @return SqlSession
     */
    public static SqlSession getSqlSession() {

        SqlSession sqlSession = sqlSessionHolder.get();
        if (sqlSession == null) {
            throw new RuntimeException("当前线程中没有SqlSession，获取失败。。。");
        }
        return sqlSession;
    }

    /**
     * 提交事务
     */
    public static void commit() {

        getSqlSession().commit();
    }

    /**
     * 关闭Session
     */
    public static void close() {

        getSqlSession().close();
        sqlSessionHolder.remove();
    }

    /**
     * 回滚
     */
    public static void rollback() {
        getSqlSession().rollback();

    }
}
