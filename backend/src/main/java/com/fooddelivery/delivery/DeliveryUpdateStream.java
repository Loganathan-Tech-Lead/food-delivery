package com.fooddelivery.delivery;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@Component
public class DeliveryUpdateStream {
    private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> subscribers = new ConcurrentHashMap<>();
    public SseEmitter subscribe(UUID orderId) {
        SseEmitter emitter = new SseEmitter(0L);
        CopyOnWriteArrayList<SseEmitter> emitters = subscribers.computeIfAbsent(orderId, ignored -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);
        emitter.onCompletion(() -> remove(orderId, emitter));
        emitter.onTimeout(() -> remove(orderId, emitter));
        emitter.onError(ignored -> remove(orderId, emitter));
        return emitter;
    }
    public void publish(UUID orderId, String eventName, Object data) {
        List<SseEmitter> emitters = subscribers.get(orderId);
        if (emitters == null) return;
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException exception) {
                remove(orderId, emitter);
            }
        }
    }
    private void remove(UUID orderId, SseEmitter emitter) {
        List<SseEmitter> emitters = subscribers.get(orderId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) subscribers.remove(orderId, emitters);
        }
    }
}