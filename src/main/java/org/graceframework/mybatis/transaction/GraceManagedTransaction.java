package org.graceframework.mybatis.transaction;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.junit.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class GraceManagedTransaction implements Transaction{

    private final DataSource dataSource;
    private TransactionIsolationLevel transactionIsolationLevel;
    private boolean autoCommit;
    private Connection connection;

    public GraceManagedTransaction(DataSource dataSource, TransactionIsolationLevel transactionIsolationLevel, boolean autoCommit) {
        Assert.assertNotNull("GraceManagedTransaction --- dataSource是null", dataSource);
        this.dataSource = dataSource;
        this.transactionIsolationLevel = transactionIsolationLevel;
        this.autoCommit = autoCommit;
    }


    @Override
    public Connection getConnection() throws SQLException {

        if (connection == null) {
            connection = dataSource.getConnection();
            int level;
            if (transactionIsolationLevel != null &&
                    (level = transactionIsolationLevel.getLevel()) != Connection.TRANSACTION_NONE) {
                connection.setTransactionIsolation(level);
            }
            connection.setAutoCommit(autoCommit);
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException {

        Connection conn = getThisConnection();
        if (!conn.getAutoCommit()) {
            conn.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {

        getThisConnection().rollback();
    }

    @Override
    public void close() throws SQLException {

        getThisConnection().close();
    }

    /**
     * 晚于getConnection() 调用 此时当前线程一定有connection
     * @return Connection
     */
    private Connection getThisConnection() throws SQLException {

        if (connection == null) {
            throw new RuntimeException("当前线程中没有Connection，获取连接失败。。。");
        }
        return connection;
    }
}
