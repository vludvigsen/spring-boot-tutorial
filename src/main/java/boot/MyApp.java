package boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }

    //@Bean
    //public HealthIndicator tvHealthIndicator() {
    //    // return an implementation of an AbstractHealthIndicator here
    //}
}
