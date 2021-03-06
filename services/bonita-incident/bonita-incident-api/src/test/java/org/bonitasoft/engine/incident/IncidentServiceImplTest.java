package org.bonitasoft.engine.incident;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.log.technical.TechnicalLoggerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IncidentServiceImplTest {

    private IncidentHandler handler1;

    private IncidentHandler handler2;

    private IncidentService incidentService;

    @Before
    public void before() {
        final List<IncidentHandler> handlers = new ArrayList<IncidentHandler>(2);
        handler1 = mock(IncidentHandler.class);
        handlers.add(handler1);
        handler2 = mock(IncidentHandler.class);
        handlers.add(handler2);
        incidentService = new IncidentServiceImpl(mock(TechnicalLoggerService.class), handlers);
    }

    @Test
    public void reportCallAllHandlers() {
        final Incident incident = new Incident("test", "recovery", null, null);
        incidentService.report(1, incident);
        verify(handler1, times(1)).handle(1, incident);
        verify(handler2, times(1)).handle(1, incident);
    }

    @Test
    public void reportCallAllHandlersEvenIfFirstThrowException() {
        doThrow(new RuntimeException()).when(handler1).handle(eq(1), any(Incident.class));
        final Incident incident = new Incident("test", "recovery", null, null);
        incidentService.report(1, incident);
        verify(handler2, times(1)).handle(1, incident);
    }

}
