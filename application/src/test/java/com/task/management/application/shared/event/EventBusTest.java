package com.task.management.application.shared.event;

import com.task.management.application.shared.EventHandlingException;
import com.task.management.domain.shared.event.DomainEvent;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventBusTest {

    private EventBus eventBus;
    private DomainEventHandler<TestEvent> handler;

    @BeforeEach
    void setUp() {
        eventBus = new EventBus();
        handler = mock(DomainEventHandler.class);
        when(handler.eventType()).thenReturn(TestEvent.class);

        eventBus.register(List.of(handler));
    }

    @Test
    void dispatch_shouldPassEventToHandler_whenAllConditionsMet() throws EventHandlingException {
        TestEvent givenEvent = new TestEvent(new Random().nextLong());
        eventBus.dispatch(givenEvent);
        verify(handler).handle(givenEvent);
    }

    @Test
    void dispatch_shouldThrowEventHandlingException_whenHandlerNotFound() {
        UnknownEvent givenEvent = new UnknownEvent();
        assertThrows(EventHandlingException.class, () -> eventBus.dispatch(givenEvent));
    }

    @Data
    private static class TestEvent implements DomainEvent {
        private final Long id;

        public TestEvent(Long id) {
            this.id = id;
        }
    }

    private static class UnknownEvent implements DomainEvent {}
}