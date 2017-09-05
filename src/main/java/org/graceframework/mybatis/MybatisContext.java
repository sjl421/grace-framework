package org.graceframework.mybatis;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.graceframework.InstanceFactory;
import org.graceframework.mybatis.tx.GraceManagedTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Tony Liu on 2017/9/4.
 * 配置mybatis configuration
 */
public class MybatisContext {

    private static final Logger logger = LoggerFactory.getLogger(MybatisContext.class);

    private static final String MYBATIS_LOG = "SLF4J";
    private static final String MYBATIS_SQLSESSIONFACTORY_NAME = "First_SqlSession_Factory";

    private static final String MYBATIS_MAPPERLOCATIONS = "com/test/demo/mybatis/mapping";
    private static final String MYBATIS_BASEPACKAGE = "com.test.demo.mybatis.dao";


    private static SqlSessionFactory sqlSessionFactory;

    static {
        DataSource dataSource = InstanceFactory.getDataSource();
        if (dataSource != null) {
            TransactionFactory transactionFactory = new GraceManagedTransactionFactory();
            Environment environment = new Environment(MYBATIS_SQLSESSIONFACTORY_NAME, transactionFactory, dataSource);
            Configuration configuration = new Configuration(environment);
            configuration.setLogImpl(configuration.getTypeAliasRegistry().resolveAlias(MYBATIS_LOG));
            if (!MYBATIS_MAPPERLOCATIONS.equals("")) {
                String[] resources = MYBATIS_MAPPERLOCATIONS.split(",");
                for (String res : resources) {
                    try {
                        registerXml(res, configuration);
                    } catch (Exception e) {
                        logger.debug("注册xml[ {} ]失败，失败信息: {}" , res, e.getMessage());
                    }
                }
            }
            if (!MYBATIS_BASEPACKAGE.equals("")) {
                String[] packages = MYBATIS_BASEPACKAGE.split(",");
                for (String pk : packages) {
                    try {
                        configuration.addMappers(pk);
                    } catch (Exception e) {
                        logger.debug("注册包[ {} ]失败，失败信息: {}" , pk, e.getMessage());
                    }
                }
            }
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        }
    }

    /**
     * 注册mapper.xml
     */
    private static void registerXml(String res, Configuration configuration) {
        if (res.toLowerCase().endsWith(".xml")) {
            InputStream inputStream = null;
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("正在注册xml {}" , res);
                }
                inputStream = Resources.getResourceAsStream(res);
                XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, res, configuration.getSqlFragments());
                mapperParser.parse();
            } catch (Exception e) {
                logger.error(e.getMessage());
                //不处理
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        //不处理
                        logger.error(e.getMessage());
                    }
                }
            }
        } else {
            try {
                URL url = Resources.getResourceURL(res);
                String resUrl = res;
                if (!resUrl.endsWith("/")) {
                    resUrl += "/";
                }
                if (url != null && url.getPath() != null) {
                    File[] files = new File(url.getPath()).listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {

                            return name.toLowerCase().endsWith(".xml");
                        }
                    });
                    if (files != null) {
                        for (File file : files) {
                            registerXml(resUrl + file.getName(), configuration);
                        }
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 此时SqlSessionFactory对象已经创建
     * 获取 SqlSessionFactory
     */
    static SqlSessionFactory getSqlSessionFactory() {

        if (sqlSessionFactory == null) {
            throw new RuntimeException("sqlSessionFactory是null，获取失败。。。");
        }
        return sqlSessionFactory;
    }

    /**
     * 获取Mapper
     */
    public static <T> T getMapper(Class<T> type) {
        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
        if (sqlSession == null) {
            return null;
        }
        return sqlSession.getMapper(type);
    }

}
