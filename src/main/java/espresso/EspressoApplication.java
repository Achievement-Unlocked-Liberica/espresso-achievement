package espresso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "espresso")
@EnableAspectJAutoProxy
@EnableConfigurationProperties
public class EspressoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EspressoApplication.class, args);
	}


	
}
