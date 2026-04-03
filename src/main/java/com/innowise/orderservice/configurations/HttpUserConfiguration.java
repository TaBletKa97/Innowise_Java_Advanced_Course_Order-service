package com.innowise.orderservice.configurations;

import com.innowise.orderservice.external.UserHttpClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(basePackages = "com.innowise.orderservice.external", types = UserHttpClient.class)
public class HttpUserConfiguration {
}
