package datchat.model.annotation;

import datchat.model.common.MessageType;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

public class PayloadSubTypeAnnotationCollector {
    private static final String BASE_PACKAGE = "datchat";

    public static Map<MessageType, Class<?>> collect() {
        Reflections reflections = new Reflections(BASE_PACKAGE);

        return reflections.getTypesAnnotatedWith(PayloadSubType.class).stream()
                .collect(HashMap::new,
                        (map, clazz) -> map.put(getMessageType(clazz), clazz),
                        HashMap::putAll);
    }

    private static MessageType getMessageType(Class<?> clazz) {
        PayloadSubType annotation = clazz.getAnnotation(PayloadSubType.class);
        return annotation.value();
    }
}
