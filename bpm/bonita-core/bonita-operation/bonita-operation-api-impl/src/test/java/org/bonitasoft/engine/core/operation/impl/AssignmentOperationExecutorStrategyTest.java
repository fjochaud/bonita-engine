package org.bonitasoft.engine.core.operation.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.bonitasoft.engine.core.expression.control.model.SExpressionContext;
import org.bonitasoft.engine.core.operation.exception.SOperationExecutionException;
import org.bonitasoft.engine.core.operation.model.SLeftOperand;
import org.bonitasoft.engine.core.operation.model.SOperation;
import org.bonitasoft.engine.data.instance.api.DataInstanceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentOperationExecutorStrategyTest {

    @Mock
    private DataInstanceService dataInstanceService;

    @InjectMocks
    private AssignmentOperationExecutorStrategy assignmentOperationExecutorStrategy;

    private SLeftOperand leftOperand;

    private SOperation operation;

    private String value;

    private SExpressionContext expressionContext;

    @Test
    public void testGetValue() throws Exception {
        initMocks();
        when(expressionContext.getInputValues()).thenReturn(Collections.<String, Object> singletonMap("var", "value"));
        when(leftOperand.isExternal()).thenReturn(false);
        Object returnedValue = assignmentOperationExecutorStrategy.getValue(operation, value, 1, "type", expressionContext);
        assertEquals("value", returnedValue);
    }

    @Test
    public void testGetValueOnExternalData() throws Exception {
        initMocks();
        // return type is not compatible
        when(expressionContext.getInputValues()).thenReturn(Collections.<String, Object> singletonMap("var", new java.util.TreeMap<String, Object>()));
        when(leftOperand.isExternal()).thenReturn(true);
        Object returnedValue = assignmentOperationExecutorStrategy.getValue(operation, value, 1, "type", expressionContext);
        assertEquals("value", returnedValue);
    }

    @Test(expected = SOperationExecutionException.class)
    public void testGetValueWithWrongType() throws Exception {
        initMocks();
        // return type is not compatible
        when(expressionContext.getInputValues()).thenReturn(Collections.<String, Object> singletonMap("var", new java.util.TreeMap<String, Object>()));
        when(leftOperand.isExternal()).thenReturn(false);
        assignmentOperationExecutorStrategy.getValue(operation, value, 1, "type", expressionContext);
    }

    private void initMocks() {
        operation = mock(SOperation.class);
        value = "value";
        expressionContext = mock(SExpressionContext.class);
        leftOperand = mock(SLeftOperand.class);
        when(operation.getLeftOperand()).thenReturn(leftOperand);
        when(leftOperand.getName()).thenReturn("var");
    }
}
