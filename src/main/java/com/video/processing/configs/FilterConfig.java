package com.video.processing.configs;

import com.video.processing.repositories.AuthTokenRepository;
import com.video.processing.repositories.UserRepository;
import com.video.processing.utilities.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class FilterConfig {

    Logger logger = Logger.getLogger(FilterConfig.class.getName());

    @Bean
    public AuthFilter authFilterBean(AuthTokenRepository tokenRepo, UserRepository userRepository) {
        return new AuthFilter(tokenRepo, userRepository);
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilter(AuthFilter filter) {
        logger.info("hello from filterRegistrationBean...");
        FilterRegistrationBean<AuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        bean.addUrlPatterns("/api/*");
        return bean;
    }
}
