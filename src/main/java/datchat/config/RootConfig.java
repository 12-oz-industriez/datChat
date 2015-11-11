package datchat.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoDatabase;
import datchat.config.heroku.HerokuConfig;
import datchat.config.local.LocalConfig;
import datchat.json.ObjectIdSerializer;
import io.vertx.core.json.Json;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@ComponentScan(basePackages = "datchat")
@Import({ HerokuConfig.class, LocalConfig.class, VertxConfig.class })
public class RootConfig {

    @Inject
    private MongoClient mongoClient;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = Json.mapper;

        SimpleModule module = new SimpleModule();
        module.addSerializer(new ObjectIdSerializer());
        objectMapper.registerModule(module);

        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, false);

        return objectMapper;
    }

    @Bean
    public MongoDatabase mongoDatabase() {
        String databaseName = "datchat";
        return mongoClient.getDatabase(databaseName);
    }
}
