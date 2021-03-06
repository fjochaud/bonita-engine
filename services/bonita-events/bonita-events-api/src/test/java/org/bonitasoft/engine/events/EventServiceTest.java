package org.bonitasoft.engine.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bonitasoft.engine.events.model.FireEventException;
import org.bonitasoft.engine.events.model.HandlerRegistrationException;
import org.bonitasoft.engine.events.model.HandlerUnregistrationException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Christophe Havard
 * @author Laurent Vaills
 */
public abstract class EventServiceTest {

    private EventService eventSvc;

    // Test events
    private final String EVT_INTERESTING = "INTERESTING";

    private final String EVT_IRRELEVANT = "IRRELEVANT";

    @Before
    public void beforeEachTest() {
        eventSvc = instantiateEventServiceImplementation();
    }

    /**
     * @return
     */
    protected abstract EventService instantiateEventServiceImplementation();

    @Test(expected = FireEventException.class)
    public void fireNullEvent() throws Exception {
        eventSvc.fireEvent(null);
    }

    @Test(expected = HandlerRegistrationException.class)
    public void addHandlerNull() throws Exception {
        eventSvc.addHandler(EVT_INTERESTING, null);
    }

    @Test(expected = HandlerRegistrationException.class)
    public void addTwiceTheSameHandler() throws Exception {
        final TestHandler h = new TestHandler();
        eventSvc.addHandler(EVT_INTERESTING, h);
        assertEquals(1, eventSvc.getHandlers(EVT_INTERESTING).size());
        eventSvc.addHandler(EVT_INTERESTING, h);
    }

    @Test
    public void addHandlerInEventFilters() throws Exception {
        final TestHandler h = new TestHandler();
        eventSvc.addHandler(EVT_INTERESTING, h);
        assertTrue(eventSvc.getHandlers(EVT_INTERESTING).contains(h));
        eventSvc.removeHandler(EVT_INTERESTING, h);
    }

    @Test
    public void addNewTypeInRegisteredHandlers() throws Exception {
        assertTrue(eventSvc.getHandlers(EVT_INTERESTING).isEmpty());

        final TestHandler h = new TestHandler();
        eventSvc.addHandler(EVT_INTERESTING, h);
        assertFalse(eventSvc.getHandlers(EVT_INTERESTING).isEmpty());

        eventSvc.removeHandler(EVT_INTERESTING, h);
    }

    @Test(expected = HandlerUnregistrationException.class)
    public void removeUnknownHandler() throws Exception {
        final TestHandler h = new TestHandler();
        eventSvc.removeHandler(EVT_INTERESTING, h);
    }

    @Test
    public void removeHandler() throws Exception {
        final TestHandler h = new TestHandler();
        eventSvc.addHandler(EVT_INTERESTING, h);
        assertEquals(1, eventSvc.getHandlers(EVT_INTERESTING).size());

        eventSvc.removeHandler(EVT_INTERESTING, h);
        assertEquals(0, eventSvc.getHandlers(EVT_INTERESTING).size());
    }

    @Test
    public void isEventFiltered() throws HandlerRegistrationException, FireEventException, HandlerUnregistrationException {
        // Register handler on a given event type.
        final TestHandlerCallback h = new TestHandlerCallback();
        eventSvc.addHandler(EVT_INTERESTING, h);

        // Fire 2 different events
        final TestEvent interesting = new TestEvent(EVT_INTERESTING);
        final TestEvent irrelevant = new TestEvent(EVT_IRRELEVANT);
        eventSvc.fireEvent(interesting);
        eventSvc.fireEvent(irrelevant);

        // Check that only "interesting" events have been received by the registered handler
        assertFalse(irrelevant.isFlagged());
        assertTrue(interesting.isFlagged());

        eventSvc.removeHandler(EVT_INTERESTING, h);
    }

    @Test
    public void isEventReceivedByHandler() throws FireEventException, HandlerRegistrationException, HandlerUnregistrationException {
        final TestHandlerCallback h = new TestHandlerCallback();
        final TestEvent interesting = new TestEvent(EVT_INTERESTING);

        eventSvc.addHandler(EVT_INTERESTING, h);
        eventSvc.fireEvent(interesting);

        assertTrue(interesting.isFlagged());
        eventSvc.removeHandler(EVT_INTERESTING, h);
    }

    @Test
    public void getAllHandlersByEvent() throws HandlerRegistrationException, HandlerUnregistrationException {
        assertTrue(eventSvc.getHandlers(EVT_INTERESTING).isEmpty());

        // add 2 different handlers for 1 event type
        final TestHandler h1 = new TestHandler();
        final TestHandler h2 = new TestHandler();

        eventSvc.addHandler(EVT_INTERESTING, h1);
        eventSvc.addHandler(EVT_INTERESTING, h2);

        // now i check if the evtList contains my both handlers
        assertFalse(eventSvc.getHandlers(EVT_INTERESTING).isEmpty());

        eventSvc.removeHandler(EVT_INTERESTING, h1);
        eventSvc.removeHandler(EVT_INTERESTING, h2);
    }

}
