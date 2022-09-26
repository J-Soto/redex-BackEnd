package pucp.dp1.redex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class RedexApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedexApplication.class, args);
	}

}
