package espresso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "espresso")
public class EspressoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EspressoApplication.class, args);
	}


	
}
