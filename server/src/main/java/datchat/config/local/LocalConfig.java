package datchat.config.local;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalConfig {

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Bean(name = "port")
    public Integer port() {
        return 8080;
    }
}
