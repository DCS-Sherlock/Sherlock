package uk.ac.warwick.dcs.sherlock.engine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import uk.ac.warwick.dcs.sherlock.api.event.IEvent;
import uk.ac.warwick.dcs.sherlock.engine.EventBus.EventInvocation;
import uk.ac.warwick.dcs.sherlock.module.model.base.ModuleModelBase;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventBusTest {

    @BeforeAll
    static void setupAll(){
    }

    @Test
    void publishEvent() {
        TestHandler handler = new TestHandler();
        TestEvent event = new TestEvent("The string before setString() has not been changed");
        try{
            Method setString = handler.getClass().getMethod("handle", TestEvent.class);
            EventBus eb = new EventBus();
            Method m = eb.getClass().getDeclaredMethod("addInvocation", Class.class , EventInvocation.class );
            m.setAccessible(true);
            EventInvocation ei = new EventInvocation(setString, handler);
            m.invoke(eb, event.getClass(), ei);
            eb.publishEvent(event);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        assertEquals( TestEvent.EVENTSTRING, event.getString(), "Strings are not equal");
    }

    @Disabled("Not implemented yet")
    @Test
    void registerEventSubscriber() {
    }

    @Test
    void registerModule() {
        EventBus eb = new EventBus();
        eb.registerModule(ModuleModelBase.class);
        int count = 0;
        try {
            Field eventMapField = eb.getClass().getDeclaredField("eventMap");
            eventMapField.setAccessible(true);
            Map<Class<? extends IEvent>, List<EventInvocation>> map = (Map<Class<? extends IEvent>, List<EventInvocation>>) eventMapField.get(eb);
            count = map.size();
        } catch (Exception e) {
            e.printStackTrace();
            count = -1;
        }
        assertTrue(count > 0, "No of event handlers registered = " + count);
    }

    @Test
    void removeInvocationsOfEvent() {
        TestHandler handler = new TestHandler();
        TestEvent event = new TestEvent("The string before setString() has not been changed");
        try{
            Method setString = handler.getClass().getMethod("handle", TestEvent.class);
            EventBus eb = new EventBus();
            Method m = eb.getClass().getDeclaredMethod("addInvocation", Class.class , EventInvocation.class );
            m.setAccessible(true);
            EventInvocation ei = new EventInvocation(setString, handler);
            m.invoke(eb, event.getClass(), ei);
            eb.publishEvent(event);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public static class TestEvent implements IEvent {

        public static final String EVENTSTRING = "Event to change string has been successfully invoked";
        private String string;

        public TestEvent(String s) {
            this.string = s;
        }

        public void setString(){
            this.string = EVENTSTRING;
        }

        public String getString() {
            return this.string;
        }
    }

    public static class TestHandler{
        public TestHandler() {
        }

        public void handle(TestEvent event){
            event.setString();
        }
    }
}

