package td.h;

import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HApplication {

    public static void main(String[] args) {
        SpringApplication.run(HApplication.class, args);
    }

    @Bean
    public OkHttpClient okHttpClient(){
        return new OkHttpClient();
    }

}

