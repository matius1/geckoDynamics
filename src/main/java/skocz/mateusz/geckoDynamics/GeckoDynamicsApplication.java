package skocz.mateusz.geckoDynamics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class GeckoDynamicsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeckoDynamicsApplication.class, args);
	}

}
