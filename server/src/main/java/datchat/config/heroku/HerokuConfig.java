package datchat.config.heroku;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("heroku")
public class HerokuConfig {

    @Value("#{systemEnvironment['MONGOLAB_URI']}")
    private String mongoConnectionString;

    @Value("#{systemEnvironment['PORT']}")
    private Integer port;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoConnectionString);
    }

    @Bean(name = "port")
    public Integer herokuPort() {
        return this.port;
    }
}
