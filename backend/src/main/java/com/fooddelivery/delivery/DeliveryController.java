package com.fooddelivery.delivery;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@RestController
@RequestMapping("/api/deliveries/{orderId}")
public class DeliveryController {
    private final DeliveryService service;
    public DeliveryController(DeliveryService service) { this.service = service; }
    @GetMapping
    public DeliveryResponses.DeliveryResponse get(@PathVariable UUID orderId) { return service.get(orderId); }
    @GetMapping("/location")
    public DeliveryResponses.LocationResponse location(@PathVariable UUID orderId) { return service.location(orderId); }
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events(@PathVariable UUID orderId) { return service.subscribe(orderId); }
}