package org.graceframework.datasource;

import javax.sql.DataSource;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public abstract class AbstractDataSource implements DataSourceFactory{



    @Override
    public DataSource getDataSource() {
        DataSource dataSource = dataSourceInstance();
        return dataSource;
    }

    public abstract DataSource dataSourceInstance();
}
