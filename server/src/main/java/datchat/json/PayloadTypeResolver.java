package datchat.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import datchat.model.annotation.PayloadSubTypeAnnotationCollector;
import datchat.model.common.RequestMessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PayloadTypeResolver extends TypeIdResolverBase {
    private Map<RequestMessageType, Class<?>> typeFromIdMapping;
    private Map<Class<?>, RequestMessageType> idFromTypeMapping;

    @Override
    public void init(JavaType bt) {
        typeFromIdMapping = PayloadSubTypeAnnotationCollector.collect();

        idFromTypeMapping = new HashMap<>();
        for (Entry<RequestMessageType, Class<?>> entry : typeFromIdMapping.entrySet()) {
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
        RequestMessageType responseMessageType = RequestMessageType.valueOf(id);
        Class<?> payloadClass = typeFromIdMapping.get(responseMessageType);

        return context.constructType(payloadClass);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }
}
