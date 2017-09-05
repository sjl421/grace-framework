package org.graceframework.datasource;

import javax.sql.DataSource;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public interface DataSourceFactory {

    /**
     * 获取数据源
     * @return 数据源
     */
    DataSource getDataSource();
}
