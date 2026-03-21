package bridge.bridge_bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReserveRepeatTransferWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReserveRepeatTransferWorkerApplication.class, args);
	}

}
