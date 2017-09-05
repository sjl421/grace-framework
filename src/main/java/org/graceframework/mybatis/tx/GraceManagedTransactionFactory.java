package org.graceframework.mybatis.tx;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class GraceManagedTransactionFactory implements TransactionFactory {
    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public Transaction newTransaction(Connection connection) {

        throw new UnsupportedOperationException("这个方法不允许使用。。。");
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel transactionIsolationLevel, boolean autoCommit) {

        return new GraceManagedTransaction(dataSource, transactionIsolationLevel, autoCommit);
    }
}
