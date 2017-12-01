package ca.on.wsib.digital.projectx.claims.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ca.on.wsib.digital.projectx.claims")
public class FeignConfiguration {

}
