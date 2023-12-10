package org.qwb.ai.faceRecognition.config;

import org.qwb.ai.common.config.db.mysql.BaseMysqlConfig;
import org.qwb.ai.common.repository.AttachRepository;
import org.qwb.ai.common.support.SimpleJpaRepositoryImpl;
import org.qwb.ai.faceRecognition.repository.FaceImageRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackageClasses = { FaceImageRepository.class, AttachRepository.class},
    repositoryBaseClass = SimpleJpaRepositoryImpl.class,
    entityManagerFactoryRef = "basicEntityManagerFactory",
    transactionManagerRef = "basicTransactionManager"
)
public class BaseDBConfig extends BaseMysqlConfig {
    
}
