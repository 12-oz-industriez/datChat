package datchat.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import datchat.model.chat.common.BaseMessage;
import datchat.model.chat.common.MessageType;
import datchat.reflection.PayloadSubTypeAnnotationCollector;

import java.util.HashMap;
import java.util.Map;

public class PayloadTypeResolver extends TypeIdResolverBase {
    private Map<MessageType, Class<? extends BaseMessage>> typeFromIdMapping;
    private Map<Class<? extends BaseMessage>, MessageType> idFromTypeMapping;

    @Override
    public void init(JavaType bt) {
        typeFromIdMapping = PayloadSubTypeAnnotationCollector.collect();
        idFromTypeMapping = typeFromIdMapping.entrySet().stream()
                .collect(HashMap::new,
                        (map, entry) -> map.put(entry.getValue(), entry.getKey()),
                        HashMap::putAll);
    }

    @Override
    public String idFromValue(Object value) {
        Class<?> clazz = value.getClass();
        if (!BaseMessage.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("Unknown type");
        }

        return idFromTypeMapping.get(clazz).toString();
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        MessageType messageType = MessageType.valueOf(id);
        Class<? extends BaseMessage> payloadClass = typeFromIdMapping.get(messageType);

        return context.constructType(payloadClass);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }
}
