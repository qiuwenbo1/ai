package org.qwb.ai.common.config.db.mysql;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.Setter;
import org.qwb.ai.common.config.auditor.MySQLAuditorProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Setter
@Component
@EnableJpaAuditing(auditorAwareRef = "baseMySQLAuditorProvider")
@ConfigurationProperties("mysql.default")
public class BaseMysqlConfig {

    private String[] packages;

    private String url;
    private String username;
    private String password;
    private String driverClassName;

    @Primary
    @Bean(name = "baseMySQLAuditorProvider")
    public AuditorAware<Long> baseMySQLAuditorProvider() {
        return new MySQLAuditorProvider();
    }

//    @Primary
//    @Bean(name = "basicDataSourceProperties")
//    @ConfigurationProperties("mysql.default")
//    public DataSourceProperties basicDataSourceProperties() {
//        return new DataSourceProperties();
//    }

    @Primary
    @Bean(name = "basicDataSource")
    @ConfigurationProperties("mysql.default.configuration")
    public DataSource basicDataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setUrl(url);
        ds.setDriverClassName(driverClassName);

        ds.setMinIdle(10);
        ds.setTestOnBorrow(true);
        ds.setTestWhileIdle(true);
        ds.setValidationQuery("select 1");
        ds.setValidationQueryTimeout(10);
        ds.setTimeBetweenEvictionRunsMillis(3600000);
        ds.setMaxActive(20);

        return ds;
    }

    @Primary
    @Bean(name = "basicEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean basicEntityManagerFactory(
            EntityManagerFactoryBuilder basicEntityManagerFactoryBuilder, @Qualifier("basicDataSource") DataSource basicDataSource) {

        Map<String, String> basicJpaProperties = new HashMap<>();
        basicJpaProperties.put("hibernate.hbm2ddl.auto", "update");

        return basicEntityManagerFactoryBuilder
                .dataSource(basicDataSource)
                .packages(packages)
                .persistenceUnit("basicDataSource")
                .properties(basicJpaProperties)
                .build();
    }

    @Primary
    @Bean(name = "basicTransactionManager")
    public PlatformTransactionManager basicTransactionManager(
            @Qualifier("basicEntityManagerFactory") EntityManagerFactory basicEntityManagerFactory) {

        return new JpaTransactionManager(basicEntityManagerFactory);
    }
    
}
