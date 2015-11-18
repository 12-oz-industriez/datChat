package datchat.model.chat.annotation;

import datchat.model.chat.common.BaseMessage;
import datchat.model.chat.common.MessageType;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PayloadSubTypeAnnotationCollector {
    private static final String BASE_PACKAGE = "datchat";

    public static Map<MessageType, Class<? extends BaseMessage>> collect() {
        Reflections reflections = new Reflections(BASE_PACKAGE);

        Set<Class<?>> types = reflections.getTypesAnnotatedWith(PayloadSubType.class);

        Map<MessageType, Class<? extends BaseMessage>> result = new HashMap<>();
        for (Class<?> clazz : types) {
            result.put(getMessageType(clazz), (Class<? extends BaseMessage>) clazz);
        }
        return result;
    }

    private static MessageType getMessageType(Class<?> clazz) {
        PayloadSubType annotation = clazz.getAnnotation(PayloadSubType.class);
        return annotation.value();
    }
}
