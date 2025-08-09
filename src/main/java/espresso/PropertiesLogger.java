package espresso;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Component
public class PropertiesLogger{

    private static final Logger log = LoggerFactory.getLogger(PropertiesLogger.class);

    // @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent  event) {
        ConfigurableEnvironment environment = (ConfigurableEnvironment) event.getApplicationContext().getEnvironment();
        System.out.println("--- Application Properties ---");
        environment.getPropertySources().forEach(ps -> {
            if (ps instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> eps = (EnumerablePropertySource<?>) ps;
                Arrays.stream(eps.getPropertyNames())
                        .forEach(name -> System.out.println(name + " = " + environment.getProperty(name)));
            }
        });
        System.out.println("-----------------------------");
    }
}
