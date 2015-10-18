package datchat.reflection;

import datchat.model.chat.annotation.PayloadSubType;
import datchat.model.chat.common.BaseMessage;
import datchat.model.chat.common.MessageType;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

public class PayloadSubTypeAnnotationCollector {
    private static final String BASE_PACKAGE = "datchat";

    public static Map<MessageType, Class<? extends BaseMessage>> collect() {
        Reflections reflections = new Reflections(BASE_PACKAGE);

        return reflections.getTypesAnnotatedWith(PayloadSubType.class).stream()
                .filter(BaseMessage.class::isAssignableFrom)
                .collect(HashMap::new,
                        (map, clazz) -> map.put(getMessageType(clazz), (Class<? extends BaseMessage>)clazz),
                        HashMap::putAll);

    }

    private static MessageType getMessageType(Class<?> clazz) {
        PayloadSubType annotation = clazz.getAnnotation(PayloadSubType.class);
        return annotation.value();
    }
}
