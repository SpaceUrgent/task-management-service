package com.task.management.application.common.event;

import com.task.management.application.common.EventHandlingException;
import com.task.management.application.common.DomainEventHandlerFactory;
import com.task.management.application.common.port.in.DomainEventHandlerPort;
import com.task.management.domain.shared.event.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainEventFactoryTest {

    private DomainEventHandlerFactory factory;
    private DomainEventHandlerPort<TestEvent> testHandler;

    @BeforeEach
    void setUp() {
        testHandler = new TestEventHandler();
        factory = new DomainEventHandlerFactory(List.of(testHandler));
    }

    @Test
    void shouldReturnMatchingHandler() throws EventHandlingException {
        TestEvent event = new TestEvent();
        DomainEventHandlerPort<TestEvent> handler = factory.supplyHandlerFor(event);
        assertNotNull(handler);
        assertEquals(testHandler, handler);
    }

    @Test
    void shouldThrowWhenNoHandlerFound() {
        DomainEventHandlerFactory emptyFactory = new DomainEventHandlerFactory(List.of());

        assertThrows(EventHandlingException.class, () ->
                emptyFactory.supplyHandlerFor(new TestEvent()));
    }

    private static class TestEventHandler implements DomainEventHandlerPort<TestEvent> {

        @Override
        public void handle(TestEvent event) {
            assertNotNull(event);
        }

        @Override
        public Class<TestEvent> eventType() {
            return TestEvent.class;
        }
    }

    private static class TestEvent implements DomainEvent {
    }
}