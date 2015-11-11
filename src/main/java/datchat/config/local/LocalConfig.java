package datchat.config.local;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalConfig {

    private static final String MONGO_CONNECTION_STRING = "mongodb://localhost:27017";

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(MONGO_CONNECTION_STRING);
    }

    @Bean(name = "port")
    public Integer port() {
        return 8080;
    }
}
