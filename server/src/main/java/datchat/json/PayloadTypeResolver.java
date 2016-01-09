package datchat.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import datchat.model.annotation.PayloadSubTypeAnnotationCollector;
import datchat.model.common.MessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PayloadTypeResolver extends TypeIdResolverBase {
    private Map<MessageType, Class<?>> typeFromIdMapping;
    private Map<Class<?>, MessageType> idFromTypeMapping;

    @Override
    public void init(JavaType bt) {
        typeFromIdMapping = PayloadSubTypeAnnotationCollector.collect();

        idFromTypeMapping = new HashMap<>();
        for (Entry<MessageType, Class<?>> entry : typeFromIdMapping.entrySet()) {
            idFromTypeMapping.put(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public String idFromValue(Object value) {
        Class<?> clazz = value.getClass();

        return idFromTypeMapping.get(clazz).toString();
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        MessageType messageType = MessageType.valueOf(id);
        Class<?> payloadClass = typeFromIdMapping.get(messageType);

        return context.constructType(payloadClass);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }
}
