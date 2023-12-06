package org.qwb.ai.common.config.db.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class DataSourceUtil {

        private static final Logger logger = LoggerFactory.getLogger(DataSourceUtil.class);

        /**
         * 创建默认配置的数据源
         * 
         * @param
         * @return
         */
        public static DataSource createAndConfigureDataSource(String dbName, String url, String username,
                        String password, String driverClassName) {
//                HikariDataSource ds = new HikariDataSource();
                DruidDataSource ds = new DruidDataSource();
                ds.setUsername(username);
                ds.setPassword(password);
                ds.setUrl(url);
//                ds.setJdbcUrl(url);
                ds.setDriverClassName(driverClassName);

                // HikariCP settings - could come from the master_tenant table but
                // hardcoded here for brevity
                // Maximum waiting time for a connection from the pool
                // ds.setConnectionTimeout(20000);

                // // Minimum number of idle connections in the pool
                // ds.setMinimumIdle(10);

                // // Maximum number of actual connection in the pool
                // ds.setMaximumPoolSize(20);

                // // Maximum time that a connection is allowed to sit idle in the pool
                // ds.setIdleTimeout(300000);
                // ds.setConnectionTimeout(20000);
                ds.setMinIdle(10);
                ds.setTestOnBorrow(true);
                ds.setTestWhileIdle(true);
                ds.setValidationQuery("select 1");
                ds.setValidationQueryTimeout(10);
                ds.setMaxActive(20);

//                ds.setMinimumIdle(10);
//                ds.setConnectionTimeout(20000);
//                ds.setMinimumIdle(10);
//                ds.setMaximumPoolSize(20);
//                ds.setConnectionTestQuery("select 1");
//                ds.set
//                ds.addDataSourceProperty("test-on-borrow", true);
//                ds.addDataSourceProperty("test-while-idle", true);
//                ds.addDataSourceProperty("validation-query", "select 1");
//                ds.addDataSourceProperty("validation-query-timeout", 10);



                // Setting up a pool name for each tenant datasource
                String tenantConnectionPoolName = dbName + "-connection-pool";
//                ds.setPoolName(tenantConnectionPoolName);
                ds.setName(tenantConnectionPoolName);
                logger.info("Configured datasource:" + dbName + ". Connection poolname:" + tenantConnectionPoolName);
                return ds;
        }
}
