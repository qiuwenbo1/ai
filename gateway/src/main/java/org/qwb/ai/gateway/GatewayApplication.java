package org.qwb.ai.gateway;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.qwb.ai.common.constant.AppConstant;;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class })
public class GatewayApplication {

    public static void main(String[] args) {
        AiApplicationStarter.run(AppConstant.APPLICATION_GATEWAY_NAME,GatewayApplication.class, args);
    }

}
