package com.webank.cmdb.wecmdbbox;

import com.webank.cmdb.wecmdbbox.common.ApplicationProperties;
import com.webank.cmdb.wecmdbbox.common.HttpClientProperties;
import com.webank.cmdb.wecmdbbox.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({HttpClientProperties.class})
@SpringBootApplication
@Import(AppConfig.class)
public class WecmdbBoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(WecmdbBoxApplication.class, args);
	}

}
