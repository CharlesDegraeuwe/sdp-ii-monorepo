package hogent.sdp2.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().directory("./api").ignoreIfMissing().load();
        dotenv.entries()
                .forEach(
                        e -> {
                            if (System.getenv(e.getKey()) == null) {
                                System.setProperty(e.getKey(), e.getValue());
                            }
                        });
        SpringApplication.run(BackendApplication.class, args);
    }
}
