package datchat.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoDatabase;
import datchat.config.heroku.HerokuConfig;
import datchat.config.local.LocalConfig;
import datchat.exception.ExceptionHandler;
import datchat.filters.common.MessageFilter;
import datchat.handlers.common.MessageDispatcher;
import datchat.handlers.common.MessageHandler;
import datchat.json.ObjectIdSerializer;
import datchat.model.common.MessageType;
import datchat.session.SessionManager;
import io.vertx.core.json.Json;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "datchat")
@Import({
        FilterConfig.class,
        HerokuConfig.class,
        LocalConfig.class,
        VertxConfig.class
})
public class RootConfig {

    @Inject
    private MongoClient mongoClient;

    @Inject
    private List<MessageHandler<?>> messageHandlers;

    @Inject
    private SessionManager sessionManager;

    @Inject
    private ExceptionHandler exceptionHandler;

    /*
     * Spring does not support '@Autowired' on maps.
     */
    @Resource(name = "filters")
    private Map<MessageType, List<MessageFilter>> filters;

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

    @Bean
    public MessageDispatcher messageDispatcher() {
        return new MessageDispatcher(
                this.messageHandlers,
                this.filters,
                this.exceptionHandler
        );
    }

}
