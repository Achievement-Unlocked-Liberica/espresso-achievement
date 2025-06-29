package espresso.achievement.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

@Configuration
public class ApiConfiguration {

    @Bean
    MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/achievement_api_messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
