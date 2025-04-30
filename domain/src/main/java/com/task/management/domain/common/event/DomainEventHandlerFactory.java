//package com.task.management.domain.common.event;
//
//import java.util.List;
//import java.util.Objects;
//
//public class DomainEventHandlerFactory {
//
//    private final List<DomainEventHandlerPort<? extends DomainEvent>> eventHandlers;
//
//    public DomainEventHandlerFactory(List<DomainEventHandlerPort<? extends DomainEvent>> eventHandlers) {
//        this.eventHandlers = eventHandlers;
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T extends DomainEvent> DomainEventHandlerPort<T> handle(T event) throws EventHandlingException {
//        Valida(event);
//        return (DomainEventHandlerPort<T>) eventHandlers.stream()
//                .filter(handler -> Objects.equals(event.getClass(), handler.eventType()))
//                .findAny()
//                .orElseThrow(() -> new EventHandlingException("Appropriate vent handler not found"));
//    }
//}
