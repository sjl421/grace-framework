package org.graceframework.datasource;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class DefaultDataSource extends AbstractDataSource{

    public static final String DB_URL = "jdbc:mysql://localhost:3306/t_blog";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "Sxb889961";

    @Override
    public DataSource dataSourceInstance() {

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        try {
            dataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }
}
