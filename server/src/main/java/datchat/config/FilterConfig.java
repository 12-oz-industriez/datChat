package datchat.config;

import datchat.filters.RestoreSessionFilter;
import datchat.filters.common.MessageFilter;
import datchat.model.common.RequestMessageType;
import datchat.session.SessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class FilterConfig {

    @Inject
    private SessionManager sessionManager;

    @Bean(name = "filters")
    public Map<RequestMessageType, List<MessageFilter>> filters() {
        Map<RequestMessageType, List<MessageFilter>> filters = new HashMap<>();

        filters.put(RequestMessageType.NEW_MESSAGE, newMessageFilters());
        filters.put(RequestMessageType.GET_LATEST, getLatestFilters());

        return filters;
    }

    private List<MessageFilter> newMessageFilters() {
        return Collections.singletonList(restoreSessionFilter());
    }

    private List<MessageFilter> getLatestFilters() {
        return Collections.singletonList(restoreSessionFilter());
    }

    @Bean
    public RestoreSessionFilter restoreSessionFilter() {
        return new RestoreSessionFilter(this.sessionManager);
    }
}
