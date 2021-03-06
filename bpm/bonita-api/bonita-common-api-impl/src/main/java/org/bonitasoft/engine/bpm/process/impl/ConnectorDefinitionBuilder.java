/**
 * Copyright (C) 2011-2012 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.bpm.process.impl;

import org.bonitasoft.engine.bpm.connector.ConnectorEvent;
import org.bonitasoft.engine.bpm.connector.FailAction;
import org.bonitasoft.engine.bpm.connector.impl.ConnectorDefinitionImpl;
import org.bonitasoft.engine.bpm.flownode.ActivityDefinition;
import org.bonitasoft.engine.bpm.flownode.impl.FlowElementContainerDefinitionImpl;
import org.bonitasoft.engine.expression.Expression;
import org.bonitasoft.engine.operation.LeftOperand;
import org.bonitasoft.engine.operation.Operation;
import org.bonitasoft.engine.operation.OperationBuilder;
import org.bonitasoft.engine.operation.OperatorType;

/**
 * @author Baptiste Mesta
 * @author Matthieu Chaffotte
 */
public class ConnectorDefinitionBuilder extends FlowElementContainerBuilder {

    private final ConnectorDefinitionImpl connectorDefinition;

    private final ProcessDefinitionBuilder processDefinitionBuilder;

    ConnectorDefinitionBuilder(final ProcessDefinitionBuilder processDefinitionBuilder, final FlowElementContainerDefinitionImpl container, final String name,
            final String connectorId, final String version, final ConnectorEvent activationEvent) {
        super(container, processDefinitionBuilder);
        this.processDefinitionBuilder = processDefinitionBuilder;
        connectorDefinition = new ConnectorDefinitionImpl(name, connectorId, version, activationEvent);
        container.addConnector(connectorDefinition);
    }

    ConnectorDefinitionBuilder(final ProcessDefinitionBuilder processDefinitionBuilder, final FlowElementContainerDefinitionImpl container, final String name,
            final String connectorId, final String version, final ConnectorEvent activationEvent, final ActivityDefinition activity) {
        super(container, processDefinitionBuilder);
        this.processDefinitionBuilder = processDefinitionBuilder;
        connectorDefinition = new ConnectorDefinitionImpl(name, connectorId, version, activationEvent);
        activity.addConnector(connectorDefinition);
    }

    public ConnectorDefinitionBuilder addInput(final String name, final Expression value) {
        if (value == null) {
            processDefinitionBuilder.addError("The input " + name + " of connector " + connectorDefinition.getName() + " is null");
        } else {
            connectorDefinition.addInput(name, value);
        }
        return this;
    }

    public ConnectorDefinitionBuilder addOutput(final Operation operation) {
        connectorDefinition.addOutput(operation);
        if (operation.getRightOperand() == null) {
            getProcessBuilder().addError("operation on connector " + connectorDefinition.getName() + " has no expression in left operand");
        }
        return this;
    }

    public ConnectorDefinitionBuilder addOutput(final LeftOperand leftOperand, final OperatorType type, final String operator, final String operatorInputType,
            final Expression rightOperand) {
        connectorDefinition.addOutput(new OperationBuilder().createNewInstance().setRightOperand(rightOperand).setType(type).setOperator(operator)
                .setOperatorInputType(operatorInputType).setLeftOperand(leftOperand).done());
        if (rightOperand == null) {
            getProcessBuilder().addError("operation on connector " + connectorDefinition.getName() + " has no expression in left operand");
        }
        return this;
    }

    public ConnectorDefinitionBuilder ignoreError() {
        connectorDefinition.setFailAction(FailAction.IGNORE);
        return this;
    }

    public ConnectorDefinitionBuilder throwErrorEventWhenFailed(final String errorCode) {
        connectorDefinition.setFailAction(FailAction.ERROR_EVENT);
        connectorDefinition.setErrorCode(errorCode);
        return this;
    }

}
