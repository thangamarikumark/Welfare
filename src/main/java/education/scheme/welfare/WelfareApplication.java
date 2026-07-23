package education.scheme.welfare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class WelfareApplication {
	public static void main(String[] args) {
		SpringApplication.run(WelfareApplication.class, args);
	}
}