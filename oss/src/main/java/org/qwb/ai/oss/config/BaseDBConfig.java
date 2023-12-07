package org.qwb.ai.oss.config;

import org.qwb.ai.common.config.db.mysql.BaseMysqlConfig;
import org.qwb.ai.common.support.SimpleJpaRepositoryImpl;
import org.qwb.ai.oss.repository.AttachRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackageClasses = { AttachRepository.class},
    repositoryBaseClass = SimpleJpaRepositoryImpl.class,
    entityManagerFactoryRef = "basicEntityManagerFactory",
    transactionManagerRef = "basicTransactionManager"
)
public class BaseDBConfig extends BaseMysqlConfig {
    
}
