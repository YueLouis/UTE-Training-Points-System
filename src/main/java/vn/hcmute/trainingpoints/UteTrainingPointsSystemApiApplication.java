package vn.hcmute.trainingpoints;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UteTrainingPointsSystemApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UteTrainingPointsSystemApiApplication.class, args);
	}

}
