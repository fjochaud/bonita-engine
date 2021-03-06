/**
 * Copyright (C) 2011-2013 BonitaSoft S.A.
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
package org.bonitasoft.engine.execution;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.bonitasoft.engine.api.impl.transaction.event.CreateEventInstance;
import org.bonitasoft.engine.bpm.bar.DocumentsResourcesContribution;
import org.bonitasoft.engine.bpm.connector.ConnectorDefinition;
import org.bonitasoft.engine.bpm.connector.ConnectorDefinitionWithInputValues;
import org.bonitasoft.engine.bpm.connector.ConnectorEvent;
import org.bonitasoft.engine.bpm.connector.InvalidEvaluationConnectorConditionException;
import org.bonitasoft.engine.bpm.model.impl.BPMInstancesCreator;
import org.bonitasoft.engine.bpm.process.ProcessInstanceState;
import org.bonitasoft.engine.builder.BuilderFactory;
import org.bonitasoft.engine.classloader.ClassLoaderService;
import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.commons.exceptions.SObjectNotFoundException;
import org.bonitasoft.engine.core.connector.ConnectorInstanceService;
import org.bonitasoft.engine.core.connector.ConnectorResult;
import org.bonitasoft.engine.core.connector.ConnectorService;
import org.bonitasoft.engine.core.connector.exception.SConnectorInstanceReadException;
import org.bonitasoft.engine.core.expression.control.api.ExpressionResolverService;
import org.bonitasoft.engine.core.expression.control.model.SExpressionContext;
import org.bonitasoft.engine.core.operation.OperationService;
import org.bonitasoft.engine.core.operation.model.SOperation;
import org.bonitasoft.engine.core.process.definition.ProcessDefinitionService;
import org.bonitasoft.engine.core.process.definition.SProcessDefinitionNotFoundException;
import org.bonitasoft.engine.core.process.definition.exception.SProcessDefinitionReadException;
import org.bonitasoft.engine.core.process.definition.model.SConnectorDefinition;
import org.bonitasoft.engine.core.process.definition.model.SDocumentDefinition;
import org.bonitasoft.engine.core.process.definition.model.SFlowElementContainerDefinition;
import org.bonitasoft.engine.core.process.definition.model.SFlowNodeDefinition;
import org.bonitasoft.engine.core.process.definition.model.SFlowNodeType;
import org.bonitasoft.engine.core.process.definition.model.SGatewayDefinition;
import org.bonitasoft.engine.core.process.definition.model.SGatewayType;
import org.bonitasoft.engine.core.process.definition.model.SProcessDefinition;
import org.bonitasoft.engine.core.process.definition.model.SSubProcessDefinition;
import org.bonitasoft.engine.core.process.definition.model.STransitionDefinition;
import org.bonitasoft.engine.core.process.definition.model.TransitionState;
import org.bonitasoft.engine.core.process.definition.model.event.SEndEventDefinition;
import org.bonitasoft.engine.core.process.definition.model.event.SStartEventDefinition;
import org.bonitasoft.engine.core.process.document.api.ProcessDocumentService;
import org.bonitasoft.engine.core.process.document.model.SProcessDocument;
import org.bonitasoft.engine.core.process.document.model.builder.SProcessDocumentBuilder;
import org.bonitasoft.engine.core.process.document.model.builder.SProcessDocumentBuilderFactory;
import org.bonitasoft.engine.core.process.instance.api.ActivityInstanceService;
import org.bonitasoft.engine.core.process.instance.api.GatewayInstanceService;
import org.bonitasoft.engine.core.process.instance.api.ProcessInstanceService;
import org.bonitasoft.engine.core.process.instance.api.TokenService;
import org.bonitasoft.engine.core.process.instance.api.TransitionService;
import org.bonitasoft.engine.core.process.instance.api.event.EventInstanceService;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SActivityExecutionException;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SFlowNodeExecutionException;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SGatewayModificationException;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SGatewayNotFoundException;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SProcessInstanceCreationException;
import org.bonitasoft.engine.core.process.instance.api.states.FlowNodeState;
import org.bonitasoft.engine.core.process.instance.model.SActivityInstance;
import org.bonitasoft.engine.core.process.instance.model.SConnectorInstance;
import org.bonitasoft.engine.core.process.instance.model.SFlowElementsContainerType;
import org.bonitasoft.engine.core.process.instance.model.SFlowNodeInstance;
import org.bonitasoft.engine.core.process.instance.model.SGatewayInstance;
import org.bonitasoft.engine.core.process.instance.model.SProcessInstance;
import org.bonitasoft.engine.core.process.instance.model.SStateCategory;
import org.bonitasoft.engine.core.process.instance.model.SToken;
import org.bonitasoft.engine.core.process.instance.model.builder.SProcessInstanceBuilder;
import org.bonitasoft.engine.core.process.instance.model.builder.SProcessInstanceBuilderFactory;
import org.bonitasoft.engine.core.process.instance.model.builder.SUserTaskInstanceBuilderFactory;
import org.bonitasoft.engine.core.process.instance.model.event.SBoundaryEventInstance;
import org.bonitasoft.engine.core.process.instance.model.event.SEventInstance;
import org.bonitasoft.engine.core.process.instance.model.event.SThrowEventInstance;
import org.bonitasoft.engine.data.instance.api.DataInstanceContainer;
import org.bonitasoft.engine.events.EventService;
import org.bonitasoft.engine.events.model.HandlerRegistrationException;
import org.bonitasoft.engine.events.model.SEvent;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.execution.event.EventsHandler;
import org.bonitasoft.engine.execution.handler.SProcessInstanceHandler;
import org.bonitasoft.engine.execution.state.FlowNodeStateManager;
import org.bonitasoft.engine.execution.work.WorkFactory;
import org.bonitasoft.engine.expression.Expression;
import org.bonitasoft.engine.expression.exception.SExpressionDependencyMissingException;
import org.bonitasoft.engine.expression.exception.SExpressionEvaluationException;
import org.bonitasoft.engine.expression.exception.SExpressionTypeUnknownException;
import org.bonitasoft.engine.expression.exception.SInvalidExpressionException;
import org.bonitasoft.engine.expression.model.SExpression;
import org.bonitasoft.engine.home.BonitaHomeServer;
import org.bonitasoft.engine.log.technical.TechnicalLogSeverity;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;
import org.bonitasoft.engine.operation.Operation;
import org.bonitasoft.engine.service.ModelConvertor;
import org.bonitasoft.engine.sessionaccessor.ReadSessionAccessor;
import org.bonitasoft.engine.transaction.TransactionService;
import org.bonitasoft.engine.work.WorkRegisterException;
import org.bonitasoft.engine.work.WorkService;

/**
 * @author Baptiste Mesta
 * @author Matthieu Chaffotte
 * @author Yanyan Liu
 * @author Elias Ricken de Medeiros
 * @author Hongwen Zang
 * @author Celine Souchet
 */
public class ProcessExecutorImpl implements ProcessExecutor {

    protected final ActivityInstanceService activityInstanceService;

    private final FlowNodeExecutor flowNodeExecutor;

    private final TechnicalLoggerService logger;

    protected final ProcessInstanceService processInstanceService;

    private final TokenService tokenService;

    private final WorkService workService;

    private final ProcessDefinitionService processDefinitionService;

    private final GatewayInstanceService gatewayInstanceService;

    private final TransitionService transitionService;

    private final EventInstanceService eventInstanceService;

    protected final ClassLoaderService classLoaderService;

    protected final ExpressionResolverService expressionResolverService;

    protected final ConnectorService connectorService;

    private final OperationService operationService;

    private final ProcessDocumentService processDocumentService;

    private final ReadSessionAccessor sessionAccessor;

    protected final BPMInstancesCreator bpmInstancesCreator;

    protected final EventsHandler eventsHandler;

    private final ConnectorInstanceService connectorInstanceService;

    public ProcessExecutorImpl(final ActivityInstanceService activityInstanceService, final ProcessInstanceService processInstanceService,
            final TechnicalLoggerService logger, final FlowNodeExecutor flowNodeExecutor, final WorkService workService,
            final ProcessDefinitionService processDefinitionService, final GatewayInstanceService gatewayInstanceService,
            final TransitionService transitionService, final EventInstanceService eventInstanceService, final ConnectorService connectorService,
            final ConnectorInstanceService connectorInstanceService, final ClassLoaderService classLoaderService, final OperationService operationService,
            final ExpressionResolverService expressionResolverService, final EventService eventService,
            final Map<String, SProcessInstanceHandler<SEvent>> handlers, final ProcessDocumentService processDocumentService,
            final ReadSessionAccessor sessionAccessor, final ContainerRegistry containerRegistry, final BPMInstancesCreator bpmInstancesCreator,
            final TokenService tokenService, final EventsHandler eventsHandler, final TransactionService transactionService,
            final FlowNodeStateManager flowNodeStateManager) {
        super();
        this.activityInstanceService = activityInstanceService;
        this.processInstanceService = processInstanceService;
        this.connectorInstanceService = connectorInstanceService;
        this.tokenService = tokenService;
        this.logger = logger;
        this.flowNodeExecutor = flowNodeExecutor;
        this.workService = workService;
        this.processDefinitionService = processDefinitionService;
        this.gatewayInstanceService = gatewayInstanceService;
        this.transitionService = transitionService;
        this.eventInstanceService = eventInstanceService;
        this.connectorService = connectorService;
        this.classLoaderService = classLoaderService;
        this.operationService = operationService;
        this.expressionResolverService = expressionResolverService;
        this.processDocumentService = processDocumentService;
        this.sessionAccessor = sessionAccessor;
        this.bpmInstancesCreator = bpmInstancesCreator;
        this.eventsHandler = eventsHandler;
        // dependency injection because of circular references...
        flowNodeStateManager.setProcessExecutor(this);
        eventsHandler.setProcessExecutor(this);
        for (final Entry<String, SProcessInstanceHandler<SEvent>> handler : handlers.entrySet()) {
            try {
                eventService.addHandler(handler.getKey(), handler.getValue());
            } catch (final HandlerRegistrationException e) {
                if (logger.isLoggable(getClass(), TechnicalLogSeverity.WARNING)) {
                    logger.log(this.getClass(), TechnicalLogSeverity.WARNING, e.getMessage());
                }
                if (logger.isLoggable(getClass(), TechnicalLogSeverity.DEBUG)) {
                    logger.log(this.getClass(), TechnicalLogSeverity.DEBUG, e);
                }
            }
        }
        containerRegistry.addContainerExecutor(this);
    }

    @Override
    public FlowNodeState executeFlowNode(final long flowNodeInstanceId, final SExpressionContext contextDependency, final List<SOperation> operations,
            final long processInstanceId, final Long executerId, final Long executerDelegateId) throws SFlowNodeExecutionException {
        return flowNodeExecutor.stepForward(flowNodeInstanceId, contextDependency, operations, processInstanceId, executerId, executerDelegateId);
    }

    private List<STransitionDefinition> evaluateOutgoingTransitions(final List<STransitionDefinition> outgoingTransitionDefinitions,
            final SProcessDefinition sDefinition, final SFlowNodeInstance flowNodeInstance) throws SBonitaException {
        List<STransitionDefinition> chosenTransitionDefinitions = Collections.emptyList();
        // int nbOfTokenToMerge = 1;// may be > 1 in case of gateway
        // if is not a normal state don't create new elements
        if (SStateCategory.NORMAL.equals(flowNodeInstance.getStateCategory())) {
            final SExpressionContext sExpressionContext = new SExpressionContext(flowNodeInstance.getId(), DataInstanceContainer.ACTIVITY_INSTANCE.name(),
                    sDefinition.getId());
            if (SFlowNodeType.GATEWAY.equals(flowNodeInstance.getType())) {
                final SGatewayInstance gatewayInstance = (SGatewayInstance) flowNodeInstance;
                switch (gatewayInstance.getGatewayType()) {
                    case EXCLUSIVE:
                        chosenTransitionDefinitions = evaluateTransitionsExclusively(sDefinition, flowNodeInstance, outgoingTransitionDefinitions,
                                sExpressionContext);
                        break;
                    case INCLUSIVE:
                        chosenTransitionDefinitions = evaluateTransitionsInclusively(sDefinition, flowNodeInstance, outgoingTransitionDefinitions,
                                sExpressionContext);
                        break;
                    case PARALLEL:
                        chosenTransitionDefinitions = evaluateTransitionsParallely(outgoingTransitionDefinitions);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported gateway type: " + gatewayInstance.getGatewayType());
                }
            } else if (SFlowNodeType.BOUNDARY_EVENT.equals(flowNodeInstance.getType())) {
                chosenTransitionDefinitions = new ArrayList<STransitionDefinition>(outgoingTransitionDefinitions);
            } else {
                if (outgoingTransitionDefinitions.isEmpty()) {
                    chosenTransitionDefinitions = new ArrayList<STransitionDefinition>(1);
                } else {
                    chosenTransitionDefinitions = evaluateTransitionsForImpliciteGateway(sDefinition, flowNodeInstance, outgoingTransitionDefinitions,
                            sExpressionContext);
                }
            }
        }
        return chosenTransitionDefinitions;
    }

    private int getNumberOfTokenToMerge(final SFlowNodeInstance sFlownodeInstance) {
        if (SFlowNodeType.GATEWAY.equals(sFlownodeInstance.getType())) {
            final String hitBys = ((SGatewayInstance) sFlownodeInstance).getHitBys();
            final String nbOfTokenToMerge = hitBys.substring(GatewayInstanceService.FINISH.length());
            return Integer.parseInt(nbOfTokenToMerge);
        } else {
            return 1;
        }
    }

    private List<STransitionDefinition> evaluateTransitionsExclusively(final SProcessDefinition sDefinition, final SFlowNodeInstance flowNodeInstance,
            final List<STransitionDefinition> outgoingTransitionDefinitions, final SExpressionContext sExpressionContext) throws SBonitaException {
        final List<STransitionDefinition> chosenTransitions = new ArrayList<STransitionDefinition>(outgoingTransitionDefinitions.size());
        boolean transitionFound = false;
        for (final STransitionDefinition sTransitionDefinition : outgoingTransitionDefinitions) {
            final Boolean condition = evaluateCondition(sDefinition, sTransitionDefinition, sExpressionContext);
            if (condition == null || condition) {
                transitionFound = true;
                chosenTransitions.add(sTransitionDefinition);
                break;
            }
        }
        if (!transitionFound) {
            final STransitionDefinition defaultTransition = getDefaultTransition(sDefinition, flowNodeInstance);
            if (defaultTransition == null) {
                throw new SActivityExecutionException("There is no default transition on " + flowNodeInstance.getName()
                        + " but no outgoing transition had a valid condition");
            }
            chosenTransitions.add(defaultTransition);
            outgoingTransitionDefinitions.add(defaultTransition);
        }
        return chosenTransitions;
    }

    private List<STransitionDefinition> evaluateTransitionsInclusively(final SProcessDefinition sDefinition, final SFlowNodeInstance flowNodeInstance,
            final List<STransitionDefinition> outgoingTransitionDefinitions, final SExpressionContext sExpressionContext) throws SBonitaException {
        final List<STransitionDefinition> chosenTransitions = new ArrayList<STransitionDefinition>(outgoingTransitionDefinitions.size());
        for (final STransitionDefinition sTransitionDefinition : outgoingTransitionDefinitions) {
            final Boolean condition = evaluateCondition(sDefinition, sTransitionDefinition, sExpressionContext);
            if (condition == null || condition) {
                chosenTransitions.add(sTransitionDefinition);
            }
        }
        if (chosenTransitions.isEmpty()) {
            final STransitionDefinition defaultTransition = getDefaultTransition(sDefinition, flowNodeInstance);
            if (defaultTransition == null) {
                throw new SActivityExecutionException("There is no default transition on " + flowNodeInstance.getName()
                        + " but no outgoing transition had a valid condition");
            }
            chosenTransitions.add(defaultTransition);
            outgoingTransitionDefinitions.add(defaultTransition);
        }
        return chosenTransitions;
    }

    private List<STransitionDefinition> evaluateTransitionsForImpliciteGateway(final SProcessDefinition sDefinition, final SFlowNodeInstance flowNodeInstance,
            final List<STransitionDefinition> outgoingTransitionDefinitions, final SExpressionContext sExpressionContext) throws SBonitaException {
        final int transitionNumber = outgoingTransitionDefinitions.size();
        final List<STransitionDefinition> conditionalTransitions = new ArrayList<STransitionDefinition>(transitionNumber);
        final List<STransitionDefinition> conditionalFalseTransitions = new ArrayList<STransitionDefinition>(transitionNumber);
        final List<STransitionDefinition> chosenTransitions = new ArrayList<STransitionDefinition>(transitionNumber);
        final List<STransitionDefinition> normalTransitions = new ArrayList<STransitionDefinition>(transitionNumber);
        for (final STransitionDefinition sTransitionDefinition : outgoingTransitionDefinitions) {
            final Boolean condition = evaluateCondition(sDefinition, sTransitionDefinition, sExpressionContext);
            if (condition == null) {
                normalTransitions.add(sTransitionDefinition);
            } else {
                if (condition) {
                    conditionalTransitions.add(sTransitionDefinition);
                } else {
                    conditionalFalseTransitions.add(sTransitionDefinition);
                }
            }
        }
        if (!normalTransitions.isEmpty()) {
            chosenTransitions.addAll(normalTransitions);
        }
        if (!conditionalTransitions.isEmpty()) {
            chosenTransitions.addAll(conditionalTransitions);
        } else if (!conditionalFalseTransitions.isEmpty()) {
            final STransitionDefinition defaultTransition = getDefaultTransition(sDefinition, flowNodeInstance);
            if (defaultTransition == null) {
                throw new SActivityExecutionException("There is no default transition on " + flowNodeInstance.getName()
                        + " but no outgoing transition had a valid condition");
            }
            chosenTransitions.add(defaultTransition);
        }
        return chosenTransitions;
    }

    private List<STransitionDefinition> evaluateTransitionsParallely(final List<STransitionDefinition> outgoingTransitionDefinitions) {
        final List<STransitionDefinition> chosenTransitions = new ArrayList<STransitionDefinition>(outgoingTransitionDefinitions.size());
        for (final STransitionDefinition sTransitionDefinition : outgoingTransitionDefinitions) {
            chosenTransitions.add(sTransitionDefinition);
        }
        return chosenTransitions;
    }

    protected STransitionDefinition getDefaultTransition(final SProcessDefinition sDefinition, final SFlowNodeInstance flowNodeInstance) {
        final SFlowElementContainerDefinition processContainer = sDefinition.getProcessContainer();
        final SFlowNodeDefinition flowNode = processContainer.getFlowNode(flowNodeInstance.getFlowNodeDefinitionId());
        return flowNode.getDefaultTransition();
    }

    protected Boolean evaluateCondition(final SProcessDefinition sDefinition, final STransitionDefinition sTransitionDefinition,
            final SExpressionContext contextDependency) throws SExpressionEvaluationException, SExpressionTypeUnknownException,
            SExpressionDependencyMissingException, SInvalidExpressionException {
        // TODO find a better method to get expression on transitionDef
        final SFlowElementContainerDefinition processContainer = sDefinition.getProcessContainer();
        final STransitionDefinition transition = processContainer.getTransition(sTransitionDefinition.getName());
        final SExpression expression = transition.getCondition();
        if (expression == null) {// no condition == true but return null to say it was a transition without condition
            return null;
        }
        if (!Boolean.class.getName().equals(expression.getReturnType())) {
            throw new SExpressionEvaluationException("Condition expression must return a boolean. on transition: " + sTransitionDefinition.getName());
        }
        return (Boolean) expressionResolverService.evaluate(expression, contextDependency);
    }

    private List<SFlowNodeDefinition> getStartNodes(final SProcessDefinition definition, final long targetSFlowNodeDefinitionId) {
        return getStartNodes(definition.getProcessContainer(), targetSFlowNodeDefinitionId);
    }

    private List<SFlowNodeDefinition> getStartNodes(final SFlowElementContainerDefinition processContainer, final long targetSFlowNodeDefinitionId) {
        final Set<SFlowNodeDefinition> sFlowNodeDefinitions = processContainer.getFlowNodes();
        final List<SFlowNodeDefinition> filteredSFlowNodeDefinitions = new ArrayList<SFlowNodeDefinition>();
        for (final SFlowNodeDefinition sFlowNodeDefinition : sFlowNodeDefinitions) {
            if (isAStartNode(targetSFlowNodeDefinitionId, sFlowNodeDefinition)) {
                // do not start event sub process (not in the flow)
                filteredSFlowNodeDefinitions.add(sFlowNodeDefinition);
            }
        }
        return filteredSFlowNodeDefinitions;
    }

    private boolean isAStartNode(final long targetSFlowNodeDefinitionId, final SFlowNodeDefinition sFlowNodeDefinition) {
        if (targetSFlowNodeDefinitionId != -1) {
            // When call start by event triggers, run only the target of trigger
            // The starterId equals 0, when start process in work (See InstantiateProcessWork)
            return sFlowNodeDefinition.getId() == targetSFlowNodeDefinitionId;
        }
        // Not started by an event: start all elements that are start point and are not triggered by an event
        return sFlowNodeDefinition.getIncomingTransitions().isEmpty()
                && !(SFlowNodeType.SUB_PROCESS.equals(sFlowNodeDefinition.getType()) && ((SSubProcessDefinition) sFlowNodeDefinition).isTriggeredByEvent())
                && !SFlowNodeType.BOUNDARY_EVENT.equals(sFlowNodeDefinition.getType())
                // is not a start event or if it is, is not triggered by an event
                && (!SFlowNodeType.START_EVENT.equals(sFlowNodeDefinition.getType()) || ((SStartEventDefinition) sFlowNodeDefinition).getEventTriggers()
                        .isEmpty());
    }

    private List<SFlowNodeDefinition> getStartNodes(final SProcessDefinition definition, final long flowNodeDefinitionId, final long targetSFlowNodeDefinitionId) {
        final SSubProcessDefinition subProcDef = (SSubProcessDefinition) definition.getProcessContainer().getFlowNode(flowNodeDefinitionId);
        return getStartNodes(subProcDef.getSubProcessContainer(), targetSFlowNodeDefinitionId);
    }

    private SConnectorInstance getNextConnectorInstance(final SProcessInstance processInstance, final ConnectorEvent event)
            throws SConnectorInstanceReadException {
        final List<SConnectorInstance> connectorInstances = connectorInstanceService.getConnectorInstances(processInstance.getId(),
                SConnectorInstance.PROCESS_TYPE, event, 0, 1, ConnectorService.TO_BE_EXECUTED);
        return connectorInstances.size() == 1 ? connectorInstances.get(0) : null;
    }

    @Override
    public boolean executeConnectors(final SProcessDefinition processDefinition, final SProcessInstance sProcessInstance, final ConnectorEvent activationEvent,
            final ConnectorService connectorService) throws SBonitaException {
        final SFlowElementContainerDefinition processContainer = processDefinition.getProcessContainer();
        final long processDefinitionId = processDefinition.getId();
        final List<SConnectorDefinition> connectors = processContainer.getConnectors(activationEvent);
        if (connectors.size() > 0) {
            SConnectorInstance nextConnectorInstance;
            nextConnectorInstance = getNextConnectorInstance(sProcessInstance, activationEvent);
            if (nextConnectorInstance != null) {
                for (final SConnectorDefinition sConnectorDefinition : connectors) {
                    if (sConnectorDefinition.getName().equals(nextConnectorInstance.getName())) {
                        final Long connectorInstanceId = nextConnectorInstance.getId();
                        final String connectorDefinitionName = sConnectorDefinition.getName();
                        workService.registerWork(WorkFactory.createExecuteConnectorOfProcess(processDefinitionId, connectorInstanceId, connectorDefinitionName,
                                sProcessInstance.getId(), sProcessInstance.getRootProcessInstanceId(), activationEvent));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<SFlowNodeInstance> initializeFirstExecutableElements(final SProcessInstance sProcessInstance, final SProcessDefinition sProcessDefinition,
            final long flowNodeDefinitionId, final long targetSFlowNodeDefinitionId) {
        try {
            List<SFlowNodeDefinition> flownNodeDefinitions;
            if (flowNodeDefinitionId == -1) {
                flownNodeDefinitions = getStartNodes(sProcessDefinition, targetSFlowNodeDefinitionId);
            } else {
                flownNodeDefinitions = getStartNodes(sProcessDefinition, flowNodeDefinitionId, targetSFlowNodeDefinitionId);
            }
            long rootProcessInstanceId = sProcessInstance.getRootProcessInstanceId();
            if (rootProcessInstanceId <= 0) {
                rootProcessInstanceId = sProcessInstance.getId();
            }
            return bpmInstancesCreator.createFlowNodeInstances(sProcessDefinition.getId(), rootProcessInstanceId, sProcessInstance.getId(),
                    flownNodeDefinitions, rootProcessInstanceId, sProcessInstance.getId(), SStateCategory.NORMAL, sProcessDefinition.getId());
        } catch (final SBonitaException e) {
            if (logger.isLoggable(this.getClass(), TechnicalLogSeverity.ERROR)) {
                logger.log(this.getClass(), TechnicalLogSeverity.ERROR, e);
            }
            throw new RuntimeException(e);
        }
    }

    private SProcessInstance createProcessInstance(final SProcessDefinition sDefinition, final long starterId, final long starterDelegateId,
            final long callerId, final SFlowNodeType callerType, final long rootProcessInstanceId) throws SProcessInstanceCreationException {
        final SProcessInstanceBuilder processInstanceBuilder = BuilderFactory.get(SProcessInstanceBuilderFactory.class).createNewInstance(sDefinition)
                .setStartedBy(starterId).setStartedByDelegate(starterDelegateId).setCallerId(callerId, callerType)
                .setRootProcessInstanceId(rootProcessInstanceId);
        final SProcessInstance sProcessInstance = processInstanceBuilder.done();
        processInstanceService.createProcessInstance(sProcessInstance);
        return sProcessInstance;
    }

    protected SProcessInstance createProcessInstance(final SProcessDefinition processDefinition, final long starterId, final long starterDelegateId,
            final long callerId) throws SProcessInstanceCreationException {
        final SActivityInstance callerInstance = getCaller(callerId);
        final SProcessInstance sProcessInstance;
        if (callerInstance != null) {
            sProcessInstance = createProcessInstance(processDefinition, starterId, starterDelegateId, callerId, callerInstance.getType(),
                    callerInstance.getRootContainerId());
        } else {
            sProcessInstance = createProcessInstance(processDefinition, starterId, starterDelegateId, callerId, null, -1);
        }
        return sProcessInstance;
    }

    private SActivityInstance getCaller(final long callerId) throws SProcessInstanceCreationException {
        SActivityInstance callerInstance = null;
        if (callerId > 0) {
            try {
                callerInstance = activityInstanceService.getActivityInstance(callerId);
            } catch (final SBonitaException e) {
                throw new SProcessInstanceCreationException("Unable to get caller", e);
            }
        }
        return callerInstance;
    }

    private void executeGateway(final SProcessDefinition sProcessDefinition, final STransitionDefinition sTransitionDefinition,
            final SFlowNodeInstance flowNodeThatTriggeredTheTransition, final Long tokenRefId) throws SBonitaException {
        final long parentProcessInstanceId = flowNodeThatTriggeredTheTransition.getParentProcessInstanceId();
        long rootProcessInstanceId = flowNodeThatTriggeredTheTransition.getRootProcessInstanceId();
        final SFlowNodeDefinition sFlowNodeDefinition = processDefinitionService.getNextFlowNode(sProcessDefinition, sTransitionDefinition.getName());
        final Long processDefinitionId = sProcessDefinition.getId();

        final boolean isGateway = SFlowNodeType.GATEWAY.equals(sFlowNodeDefinition.getType());
        boolean toExecute = false;
        final Long nextFlowNodeInstanceId;
        try {
            final SProcessInstance parentProcessInstance = processInstanceService.getProcessInstance(parentProcessInstanceId);
            final SStateCategory stateCategory = parentProcessInstance.getStateCategory();
            if (isGateway) {
                final SGatewayInstance gatewayInstance = createOrRetreiveGateway(processDefinitionId, sFlowNodeDefinition, stateCategory, tokenRefId,
                        parentProcessInstanceId, rootProcessInstanceId, sTransitionDefinition);
                gatewayInstanceService.hitTransition(gatewayInstance, sFlowNodeDefinition.getTransitionIndex(sTransitionDefinition.getName()));
                nextFlowNodeInstanceId = gatewayInstance.getId();
                if (gatewayInstanceService.checkMergingCondition(sProcessDefinition, gatewayInstance)) {
                    gatewayInstanceService.setFinish(gatewayInstance);
                    toExecute = true;
                }
            } else {
                final SFlowNodeInstance flowNodeInstance = bpmInstancesCreator.toFlowNodeInstance(processDefinitionId,
                        flowNodeThatTriggeredTheTransition.getRootContainerId(), flowNodeThatTriggeredTheTransition.getParentContainerId(),
                        SFlowElementsContainerType.PROCESS, sFlowNodeDefinition, rootProcessInstanceId, parentProcessInstanceId, true, -1, stateCategory, -1,
                        tokenRefId);
                new CreateEventInstance((SEventInstance) flowNodeInstance, eventInstanceService).call();
                nextFlowNodeInstanceId = flowNodeInstance.getId();
                toExecute = true;
            }
            if (toExecute) {
                workService.registerWork(WorkFactory.createExecuteFlowNodeWork(nextFlowNodeInstanceId, null, null, parentProcessInstanceId));
            }
        } catch (final SBonitaException e) {
            if (logger.isLoggable(this.getClass(), TechnicalLogSeverity.ERROR)) {
                logger.log(this.getClass(), TechnicalLogSeverity.ERROR, e);
            }
            throw e;
        }
    }

    /*
     * try to gate active gateway.
     * if the gateway is already hit by this transition or by the same token, we create a new gateway
     */
    private SGatewayInstance createOrRetreiveGateway(final Long processDefinitionId, final SFlowNodeDefinition flowNodeDefinition,
            final SStateCategory stateCategory, final Long tokenRefId, final long parentProcessInstanceId, final long rootProcessInstanceId,
            final STransitionDefinition transitionDefinition) throws SBonitaException {
        SGatewayInstance gatewayInstance = null;
        try {
            gatewayInstance = gatewayInstanceService.getActiveGatewayInstanceOfTheProcess(parentProcessInstanceId, flowNodeDefinition.getName());
        } catch (final SGatewayNotFoundException e) {
            // no gateway found we create one
        }

        // check if it's already hit
        if (gatewayInstance != null
                && gatewayInstance.getHitBys() != null
                && (gatewayInstance.getHitBys().contains(GatewayInstanceService.FINISH) || Arrays.asList(gatewayInstance.getHitBys().split(",")).contains(
                        flowNodeDefinition.getTransitionIndex(transitionDefinition.getName())))) {
            gatewayInstance = null;// already hit we create a new one
        }
        if (gatewayInstance != null && !tokenRefId.equals(gatewayInstance.getTokenRefId())) {
            throw new SFlowNodeExecutionException("Error while executing the join on gateway <" + gatewayInstance.getName() + "> of process instance <"
                    + gatewayInstance.getParentProcessInstanceId() + "> of process definition <" + gatewayInstance.getProcessDefinitionId()
                    + ">, this can happen when you try to join branches that are not from the same split level "
                    + "(some branches were split more times than others). Check if your design is valid in the Bonita Studio." + " token is: <" + tokenRefId
                    + "> expected <" + gatewayInstance.getTokenRefId() + ">");
        }
        if (gatewayInstance != null) {
            return gatewayInstance;
        }
        return createGateway(processDefinitionId, flowNodeDefinition, stateCategory, parentProcessInstanceId, tokenRefId, rootProcessInstanceId);
    }

    private SGatewayInstance createGateway(final Long processDefinitionId, final SFlowNodeDefinition flowNodeDefinition, final SStateCategory stateCategory,
            final long parentProcessInstanceId, final Long tokenRefId, final long rootProcessInstanceId) throws SBonitaException {
        return (SGatewayInstance) bpmInstancesCreator
                .createFlowNodeInstance(processDefinitionId, rootProcessInstanceId, parentProcessInstanceId, SFlowElementsContainerType.PROCESS,
                        flowNodeDefinition, rootProcessInstanceId, parentProcessInstanceId, false, 0, stateCategory, -1, tokenRefId);
    }

    protected void executeOperations(final List<SOperation> operations, Map<String, Object> context, final SExpressionContext expressionContext,
            final SProcessInstance sProcessInstance) throws SBonitaException {
        if (operations != null && !operations.isEmpty()) {
            // Execute operation
            final long processInstanceId = sProcessInstance.getId();
            for (final SOperation operation : operations) {
                // TODO operations can be evaluated in batch here
                if (context != null) {
                    context = new HashMap<String, Object>(context);
                }
                expressionContext.setInputValues(context);
                operationService.execute(operation, processInstanceId, DataInstanceContainer.PROCESS_INSTANCE.name(), expressionContext);
            }
        }
    }

    protected boolean initialize(final long userId, final SProcessDefinition sProcessDefinition, final SProcessInstance sProcessInstance,
            SExpressionContext expressionContext, final List<SOperation> operations, final Map<String, Object> context,
            final SFlowElementContainerDefinition processContainer, final List<ConnectorDefinitionWithInputValues> connectors)
            throws SProcessInstanceCreationException {
        try {
            // Create SDataInstances
            bpmInstancesCreator.createDataInstances(sProcessInstance, processContainer, sProcessDefinition, expressionContext, operations, context);
            createDocuments(sProcessDefinition, sProcessInstance, userId);
            if (connectors != null) {
                executeConnectors(sProcessDefinition, sProcessInstance, connectors);
            }
            if (expressionContext == null) {
                expressionContext = new SExpressionContext();
            }
            executeOperations(operations, context, expressionContext, sProcessInstance);

            // Create connectors
            bpmInstancesCreator.createConnectorInstances(sProcessInstance, processContainer.getConnectors(), SConnectorInstance.PROCESS_TYPE);

            return executeConnectors(sProcessDefinition, sProcessInstance, ConnectorEvent.ON_ENTER, connectorService);
        } catch (final SProcessInstanceCreationException e) {
            throw e;
        } catch (final SBonitaException e) {
            throw new SProcessInstanceCreationException(e);
        } catch (final BonitaHomeNotSetException e) {
            throw new SProcessInstanceCreationException(e);
        } catch (final IOException e) {
            throw new SProcessInstanceCreationException(e);
        } catch (final InvalidEvaluationConnectorConditionException e) {
            throw new SProcessInstanceCreationException(e);
        }
    }

    protected void createDocuments(final SProcessDefinition sDefinition, final SProcessInstance sProcessInstance, final long authorId) throws SBonitaException,
            IOException, BonitaHomeNotSetException {
        final SFlowElementContainerDefinition processContainer = sDefinition.getProcessContainer();
        final List<SDocumentDefinition> documentDefinitions = processContainer.getDocumentDefinitions();
        if (!documentDefinitions.isEmpty()) {
            final String processesFolder = BonitaHomeServer.getInstance().getProcessFolder(sessionAccessor.getTenantId(), sDefinition.getId());
            final File documentsFolder = new File(processesFolder, DocumentsResourcesContribution.DOCUMENTS_FOLDER);
            for (final SDocumentDefinition document : documentDefinitions) {
                if (document.getFile() != null) {
                    final String file = document.getFile();// should always exists...validation on businessarchive
                    final byte[] content = FileUtils.readFileToByteArray(new File(documentsFolder, file));
                    attachDocument(sProcessInstance.getId(), document.getName(), document.getFileName(), document.getContentMimeType(), content, authorId);
                } else if (document.getUrl() != null) {
                    attachDocument(sProcessInstance.getId(), document.getName(), document.getFileName(), document.getContentMimeType(), document.getUrl(),
                            authorId);
                } else {
                    throw new SProcessInstanceCreationException("Unable to create documents, a document was defined without url or content");
                }
            }
        }
    }

    protected SProcessDocument attachDocument(final long processInstanceId, final String documentName, final String fileName, final String mimeType,
            final String url, final long authorId) throws SBonitaException {
        final SProcessDocument attachment = buildExternalProcessDocumentReference(processInstanceId, documentName, fileName, mimeType, authorId, url);
        return processDocumentService.attachDocumentToProcessInstance(attachment);
    }

    protected SProcessDocument attachDocument(final long processInstanceId, final String documentName, final String fileName, final String mimeType,
            final byte[] documentContent, final long authorId) throws SBonitaException {
        final SProcessDocument attachment = buildProcessDocument(processInstanceId, documentName, fileName, mimeType, authorId);
        return processDocumentService.attachDocumentToProcessInstance(attachment, documentContent);
    }

    private SProcessDocument buildExternalProcessDocumentReference(final long processInstanceId, final String documentName, final String fileName,
            final String mimeType, final long authorId, final String url) {
        SProcessDocumentBuilder documentBuilder = initDocumentBuilder(processInstanceId, documentName, fileName, mimeType, authorId);
        documentBuilder.setURL(url);
        documentBuilder.setHasContent(false);
        return documentBuilder.done();
    }

    private SProcessDocument buildProcessDocument(final long processInstanceId, final String documentName, final String fileName, final String mimetype,
            final long authorId) {
        SProcessDocumentBuilder documentBuilder = initDocumentBuilder(processInstanceId, documentName, fileName, mimetype, authorId);
        documentBuilder.setHasContent(true);
        return documentBuilder.done();
    }

    private SProcessDocumentBuilder initDocumentBuilder(final long processInstanceId, final String documentName, final String fileName, final String mimetype,
            final long authorId) {
        final SProcessDocumentBuilder documentBuilder = BuilderFactory.get(SProcessDocumentBuilderFactory.class).createNewInstance();
        documentBuilder.setName(documentName);
        documentBuilder.setFileName(fileName);
        documentBuilder.setContentMimeType(mimetype);
        documentBuilder.setProcessInstanceId(processInstanceId);
        documentBuilder.setAuthor(authorId);
        documentBuilder.setCreationDate(System.currentTimeMillis());
        return documentBuilder;
    }

    @Override
    public void childFinished(final long processDefinitionId, final long flowNodeInstanceId, final int stateId, final long parentId) throws SBonitaException {
        final SFlowNodeInstance sFlowNodeInstanceChild = activityInstanceService.getFlowNodeInstance(flowNodeInstanceId);
        final SProcessDefinition sProcessDefinition = processDefinitionService.getProcessDefinition(processDefinitionId);
        final SUserTaskInstanceBuilderFactory flowNodeKeyProvider = BuilderFactory.get(SUserTaskInstanceBuilderFactory.class);
        final long processInstanceId = sFlowNodeInstanceChild.getLogicalGroup(flowNodeKeyProvider.getParentProcessInstanceIndex());

        SProcessInstance sProcessInstance = processInstanceService.getProcessInstance(processInstanceId);
        final int tokensOfProcess = executeValidOutgoingTransitionsAndUpdateTokens(sProcessDefinition, sFlowNodeInstanceChild, sProcessInstance);
        if (tokensOfProcess == 0) {
            boolean hasActionsToExecute = false;
            if (ProcessInstanceState.ABORTING.getId() != sProcessInstance.getStateId()) {
                hasActionsToExecute = executePostThrowEventHandlers(sProcessDefinition, sProcessInstance, sFlowNodeInstanceChild);
                // the process instance has maybe changed
                if (hasActionsToExecute) {
                    sProcessInstance = processInstanceService.getProcessInstance(processInstanceId);
                }
                eventsHandler.unregisterEventSubProcess(sProcessDefinition, sProcessInstance);
            }
            handleProcessCompletion(sProcessDefinition, sProcessInstance, hasActionsToExecute);
        }
    }

    @Override
    public void handleProcessCompletion(final SProcessDefinition sProcessDefinition, final SProcessInstance sProcessInstance, final boolean hasActionsToExecute)
            throws SBonitaException {
        ProcessInstanceState processInstanceState;
        switch (sProcessInstance.getStateCategory()) {
            case ABORTING:
                if (ProcessInstanceState.ABORTING.getId() == sProcessInstance.getStateId()) {
                    processInstanceState = ProcessInstanceState.ABORTED;
                } else {
                    if (hasActionsToExecute) {
                        processInstanceState = ProcessInstanceState.ABORTING;
                    } else {
                        processInstanceState = ProcessInstanceState.ABORTED;
                    }
                }
                break;
            case CANCELLING:
                processInstanceState = ProcessInstanceState.CANCELLED;
                break;
            default:
                if (ProcessInstanceState.COMPLETING.getId() == sProcessInstance.getStateId()) {
                    processInstanceState = ProcessInstanceState.COMPLETED;
                } else {
                    if (executeConnectors(sProcessDefinition, sProcessInstance, ConnectorEvent.ON_FINISH, connectorService)) {
                        // some connectors were trigger
                        processInstanceState = ProcessInstanceState.COMPLETING;
                    } else {
                        processInstanceState = ProcessInstanceState.COMPLETED;
                    }
                }
                break;
        }
        processInstanceService.setState(sProcessInstance, processInstanceState);
        if (tokenService.getNumberOfToken(sProcessInstance.getId()) == 0) {
            flowNodeExecutor.childReachedState(sProcessInstance, processInstanceState, hasActionsToExecute);
        }

    }

    private boolean executePostThrowEventHandlers(final SProcessDefinition sProcessDefinition, final SProcessInstance sProcessInstance,
            final SFlowNodeInstance child) throws SBonitaException {
        boolean hasActionsToExecute = false;
        if (sProcessInstance.getInterruptingEventId() != -1) {
            final SFlowNodeInstance endEventInstance = activityInstanceService.getFlowNodeInstance(sProcessInstance.getInterruptingEventId());
            final SEndEventDefinition endEventDefinition = (SEndEventDefinition) sProcessDefinition.getProcessContainer().getFlowNode(
                    endEventInstance.getFlowNodeDefinitionId());
            hasActionsToExecute = eventsHandler.handlePostThrowEvent(sProcessDefinition, endEventDefinition, (SThrowEventInstance) endEventInstance, child);
            flowNodeExecutor.archiveFlowNodeInstance(endEventInstance, true, sProcessDefinition.getId());
        }
        return hasActionsToExecute;
    }

    /**
     * Evaluate the split of the element
     * The element contains the current token it received
     * 
     * @return
     *         number of token of the process
     */
    private int executeValidOutgoingTransitionsAndUpdateTokens(final SProcessDefinition sProcessDefinition, final SFlowNodeInstance child,
            final SProcessInstance sProcessInstance) throws SBonitaException {
        final long parentProcessInstanceId = sProcessInstance.getId();
        final long childId = child.getId();
        // token we merged
        final int numberOfTokenToMerge = getNumberOfTokenToMerge(child);
        final SFlowNodeDefinition flowNode = sProcessDefinition.getProcessContainer().getFlowNode(child.getFlowNodeDefinitionId());
        final int inputTransitionsSize;
        final List<STransitionDefinition> allOutgoingTransitionDefinitions;
        // Retrieve all outgoing transitions
        if (flowNode == null) {
            // not in definition
            inputTransitionsSize = 0;
            allOutgoingTransitionDefinitions = Collections.emptyList();
        } else {
            inputTransitionsSize = flowNode.getIncomingTransitions().size();
            allOutgoingTransitionDefinitions = new ArrayList<STransitionDefinition>(flowNode.getOutgoingTransitions());
        }
        // Evaluate all outgoing transitions, and retrieve valid outgoing transitions
        final List<STransitionDefinition> validOutgoingTransitionDefinitions = evaluateOutgoingTransitions(allOutgoingTransitionDefinitions,
                sProcessDefinition, child);

        final long rootProcessInstanceId = child.getRootProcessInstanceId();

        final List<STransitionDefinition> chosenGatewaysTransitions = new ArrayList<STransitionDefinition>(validOutgoingTransitionDefinitions.size());
        final List<SFlowNodeDefinition> chosenFlowNode = new ArrayList<SFlowNodeDefinition>(validOutgoingTransitionDefinitions.size());
        boolean consumeInputToken = false;
        Long outputTokenRefId = null;
        Long outputParentTokenRefId = null;
        boolean createToken = false;
        boolean isParalleleOrInclusive = false;
        boolean isExclusive = false;
        boolean implicitEnd = false;
        if (flowNode instanceof SGatewayDefinition) {
            final SGatewayType gatewayType = ((SGatewayDefinition) flowNode).getGatewayType();
            switch (gatewayType) {
                case EXCLUSIVE:
                    isExclusive = true;
                    break;
                case INCLUSIVE:
                    isParalleleOrInclusive = true;
                    break;
                case PARALLEL:
                    isParalleleOrInclusive = true;
                    break;
            }
        }
        if (flowNode == null) {
            // not in the definition: no merge no split no implicit end
            consumeInputToken = false;
            createToken = false;
            implicitEnd = false;
        } else if (SFlowNodeType.BOUNDARY_EVENT.equals(child.getType())) {
            // the creation of tokens for the boundaries are done inside the ExecutingBoundaryEventStateImpl
            // we don't change tokens
            consumeInputToken = false;
            createToken = false;
            implicitEnd = false;
            // still need to get the refId to put on the next element
            final SBoundaryEventInstance boundaryEventInstance = (SBoundaryEventInstance) child;
            if (boundaryEventInstance.isInterrupting()) {
                // we create the same token that activated the activity
                // the activity is canceled so a token will be consumed by the aborted activity
                final SToken token = tokenService.getToken(boundaryEventInstance.getParentProcessInstanceId(), boundaryEventInstance.getTokenRefId());
                outputTokenRefId = token.getRefId();
                outputParentTokenRefId = token.getParentRefId();
            } else {
                // a token with no parent is produced -> not the same "execution" than activity
                outputTokenRefId = boundaryEventInstance.getId();
                outputParentTokenRefId = null;
            }

        } else {

            if (validOutgoingTransitionDefinitions.size() > 0) {
                // depending on the split type and number of input/output transition we determine if token are created, with with type and if we consume
                // incoming
                // tokens
                if (isExclusive) {
                    // always transmit token
                    consumeInputToken = false;
                    outputTokenRefId = child.getTokenRefId();
                    createToken = false;
                } else {
                    if (inputTransitionsSize <= 1) {
                        if (allOutgoingTransitionDefinitions.size() <= 1) {
                            // 1 input , 1 output
                            // input token is transmit
                            consumeInputToken = false;
                            outputTokenRefId = child.getTokenRefId();
                            createToken = false;
                        } else {
                            // 1 input , >1 output
                            // create children input token
                            consumeInputToken = false;
                            outputTokenRefId = child.getId();
                            outputParentTokenRefId = child.getTokenRefId();
                            createToken = true;
                        }
                    } else {
                        if (allOutgoingTransitionDefinitions.size() <= 1) {
                            // >1 input , 1 output
                            // consume input and transmit parent token for parallele and inclusive
                            if (isParalleleOrInclusive) {
                                // consume input token + transmit parent token
                                consumeInputToken = true;
                                final SToken token = tokenService.getToken(sProcessInstance.getId(), child.getTokenRefId());
                                outputTokenRefId = token.getParentRefId();
                                createToken = false;
                            } else {
                                // implicit gateway: input token is transmit
                                consumeInputToken = false;
                                outputTokenRefId = child.getTokenRefId();
                                createToken = false;
                            }
                        } else {
                            // >1 input , >1 output
                            if (isParalleleOrInclusive) {
                                // consume input tokens and create children token having the same parent
                                consumeInputToken = true;
                                outputTokenRefId = child.getId();
                                // TODO get ParentTokenRefId
                                final SToken token = tokenService.getToken(sProcessInstance.getId(), child.getTokenRefId());
                                outputParentTokenRefId = token.getParentRefId();
                                createToken = true;
                            } else {
                                // implicit gateway: create children token of input
                                consumeInputToken = false;
                                outputTokenRefId = child.getId();
                                outputParentTokenRefId = child.getTokenRefId();
                                createToken = true;
                            }
                        }
                    }
                }
            } else {
                // implicit end
                implicitEnd = true;
                consumeInputToken = false;
                createToken = false;
            }
        }

        for (final STransitionDefinition sTransitionDefinition : allOutgoingTransitionDefinitions) {
            if (!validOutgoingTransitionDefinitions.contains(sTransitionDefinition)) {
                // Archive invalid transitions
                transitionService.archive(sTransitionDefinition, child, TransitionState.ABORTED);
            }
        }

        for (final STransitionDefinition sTransitionDefinition : validOutgoingTransitionDefinitions) {
            final SFlowNodeDefinition flowNodeDefinition = processDefinitionService.getNextFlowNode(sProcessDefinition, sTransitionDefinition.getName());
            // we archive a transition to keep a track of where the flow was
            transitionService.archive(sTransitionDefinition, child, TransitionState.TAKEN);
            if (flowNodeDefinition instanceof SGatewayDefinition) {
                chosenGatewaysTransitions.add(sTransitionDefinition);
            } else {
                // Shortcut: event or activity, we execute them directly
                chosenFlowNode.add(flowNodeDefinition);
            }
        }

        if (childId != sProcessInstance.getInterruptingEventId() || SFlowNodeType.SUB_PROCESS.equals(sProcessInstance.getCallerType())) {
            // Let's archive the final state of the child:
            flowNodeExecutor.archiveFlowNodeInstance(child, true, sProcessDefinition.getId());
        }

        // execute transition/activities
        createAndExecuteActivities(sProcessDefinition.getId(), child, parentProcessInstanceId, chosenFlowNode, rootProcessInstanceId, outputTokenRefId);
        for (STransitionDefinition sTransitionDefinition : chosenGatewaysTransitions) {
            executeGateway(sProcessDefinition, sTransitionDefinition, child, outputTokenRefId);
        }

        // handle token creation/deletion
        if (consumeInputToken) {
            tokenService.deleteTokens(sProcessInstance.getId(), child.getTokenRefId(), numberOfTokenToMerge);
        }
        if (createToken) {
            tokenService.createTokens(sProcessInstance.getId(), outputTokenRefId, outputParentTokenRefId, validOutgoingTransitionDefinitions.size());
        }
        if (implicitEnd) {
            final Long tokenRefId = child.getTokenRefId();
            if (tokenRefId == null) {
                throw new SObjectNotFoundException("no token ref id set for " + child);
            }
            implicitEnd(sProcessDefinition, sProcessInstance.getId(), numberOfTokenToMerge, tokenRefId);
        }
        return tokenService.getNumberOfToken(sProcessInstance.getId());
    }

    /**
     * execute the implicit end of an element
     * 
     * @param numberOfTokenToMerge
     * @param tokenRefId
     * @param processInstanceId
     * @param processDefinition
     * @throws SBonitaException
     * @throws WorkRegisterException
     * @throws SGatewayModificationException
     */
    private void implicitEnd(final SProcessDefinition processDefinition, final long processInstanceId, final int numberOfTokenToMerge, final Long tokenRefId)
            throws SGatewayModificationException, WorkRegisterException, SBonitaException {
        final SToken token = tokenService.getToken(processInstanceId, tokenRefId);
        tokenService.deleteTokens(processInstanceId, tokenRefId, numberOfTokenToMerge);
        /*
         * if there is inclusive gateway we check if there is one that refers to this token
         * In this case the gateway should be notified in order to recalculate if it is now merged
         */
        if (processDefinition.getProcessContainer().containsInclusiveGateway()) {
            final SGatewayInstance gatewayInstance = gatewayInstanceService.getGatewayMergingToken(processInstanceId, tokenRefId);
            if (gatewayInstance != null && gatewayInstanceService.checkMergingCondition(processDefinition, gatewayInstance)) {
                gatewayInstanceService.setFinish(gatewayInstance);
                workService.registerWork(WorkFactory.createExecuteFlowNodeWork(gatewayInstance.getId(), null, null, processInstanceId));
            }
        }
        // consume on token parent if there is one and if there is no other token having the same refId
        if (token.getParentRefId() != null && tokenService.getNumberOfToken(processInstanceId, tokenRefId) == 0) {
            implicitEnd(processDefinition, processInstanceId, 1, token.getParentRefId());
        }

    }

    @Override
    public SProcessInstance start(final SProcessDefinition sProcessDefinition, final long starterId, final long starterDelegateId,
            final List<SOperation> operations, final Map<String, Object> context, final List<ConnectorDefinitionWithInputValues> connectorsWithInput)
            throws SProcessInstanceCreationException {
        return start(sProcessDefinition, -1, starterId, starterDelegateId, null, operations, context, connectorsWithInput, -1, -1);
    }

    @Override
    public SProcessInstance start(final long processDefinitionId, final long targetSFlowNodeDefinitionId, final long starterId, final long starterDelegateId,
            final SExpressionContext expressionContext, final List<SOperation> operations, final Map<String, Object> context,
            final List<ConnectorDefinitionWithInputValues> connectorsWithInput, final long callerId, final long subProcessDefinitionId)
            throws SProcessInstanceCreationException {
        try {
            final SProcessDefinition sProcessDefinition = processDefinitionService.getProcessDefinition(processDefinitionId);
            return start(sProcessDefinition, targetSFlowNodeDefinitionId, starterId, starterDelegateId, expressionContext, operations, context,
                    connectorsWithInput, callerId, subProcessDefinitionId);
        } catch (final SProcessDefinitionNotFoundException e) {
            throw new SProcessInstanceCreationException(e);
        } catch (final SProcessDefinitionReadException e) {
            throw new SProcessInstanceCreationException(e);
        }
    }

    @Override
    public SProcessInstance start(final SProcessDefinition sProcessDefinition, final long targetSFlowNodeDefinitionId, final long starterId,
            final long starterDelegateId, final SExpressionContext expressionContext, final List<SOperation> operations, final Map<String, Object> context,
            final List<ConnectorDefinitionWithInputValues> connectors, final long callerId, final long subProcessDefinitionId)
            throws SProcessInstanceCreationException {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final ClassLoader localClassLoader = classLoaderService.getLocalClassLoader("process", sProcessDefinition.getId());
            Thread.currentThread().setContextClassLoader(localClassLoader);
            // initialize the process classloader by getting it one time
            try {
                localClassLoader.loadClass(this.getClass().getName());
            } catch (final ClassNotFoundException e) {
            }
            final SProcessInstance sProcessInstance = createProcessInstance(sProcessDefinition, starterId, starterDelegateId, callerId);

            final SFlowElementContainerDefinition processContainer = getProcessContainer(sProcessDefinition, subProcessDefinitionId);
            final boolean isInitializing = initialize(starterId, sProcessDefinition, sProcessInstance, expressionContext,
                    operations != null ? new ArrayList<SOperation>(operations) : operations, context, processContainer, connectors);
            handleEventSubProcess(sProcessDefinition, sProcessInstance, subProcessDefinitionId);
            if (isInitializing) {
                // some connectors were trigger
                processInstanceService.setState(sProcessInstance, ProcessInstanceState.INITIALIZING);
                // we stop execution here
                return sProcessInstance;
            }
            return startElements(sProcessDefinition, sProcessInstance, subProcessDefinitionId, targetSFlowNodeDefinitionId);
        } catch (final SBonitaException e) {
            throw new SProcessInstanceCreationException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    /*
     * Execute connectors then execute output operation and disconnect connectors
     */
    protected void executeConnectors(final SProcessDefinition processDefinition, final SProcessInstance sProcessInstance,
            final List<ConnectorDefinitionWithInputValues> connectorsList) throws InvalidEvaluationConnectorConditionException, SBonitaException {
        final SExpressionContext expcontext = new SExpressionContext();
        expcontext.setProcessDefinitionId(processDefinition.getId());
        expcontext.setProcessDefinition(processDefinition);
        expcontext.setContainerId(sProcessInstance.getId());
        expcontext.setContainerType(DataInstanceContainer.PROCESS_INSTANCE.name());
        for (final ConnectorDefinitionWithInputValues connectorWithInput : connectorsList) {
            final ConnectorDefinition connectorDefinition = connectorWithInput.getConnectorDefinition();
            final Map<String, Map<String, Serializable>> contextInputValues = connectorWithInput.getInputValues();
            final String connectorId = connectorDefinition.getConnectorId();
            final String version = connectorDefinition.getVersion();
            final Map<String, Expression> inputs = connectorDefinition.getInputs();
            if (contextInputValues.size() != inputs.size()) {
                throw new InvalidEvaluationConnectorConditionException(contextInputValues.size(), inputs.size());
            }
            final Map<String, SExpression> connectorsExps = ModelConvertor.constructExpressions(inputs);

            // we use the context classloader because the process classloader is already set
            final ConnectorResult result = connectorService.executeMutipleEvaluation(processDefinition.getId(), connectorId, version, connectorsExps,
                    contextInputValues, Thread.currentThread().getContextClassLoader(), expcontext);
            final List<Operation> outputs = connectorDefinition.getOutputs();
            final List<SOperation> sOperations = new ArrayList<SOperation>(outputs.size());
            for (final Operation operation : outputs) {
                sOperations.add(ModelConvertor.constructSOperation(operation));
            }
            connectorService.executeOutputOperation(sOperations, expcontext, result);
        }
    }

    protected SFlowElementContainerDefinition getProcessContainer(final SProcessDefinition processDefinition, final long subProcessDefinitionId) {
        SFlowElementContainerDefinition processContainer;
        if (subProcessDefinitionId == -1) {
            processContainer = processDefinition.getProcessContainer();
        } else {
            processContainer = ((SSubProcessDefinition) processDefinition.getProcessContainer().getFlowNode(subProcessDefinitionId)).getSubProcessContainer();
        }
        return processContainer;
    }

    protected void handleEventSubProcess(final SProcessDefinition processDefinition, final SProcessInstance sProcessInstance, final long subProcessDefinitionId)
            throws SActivityExecutionException {
        if (subProcessDefinitionId == -1) {
            try {
                // modify that to support event sub-processes within sub-processes
                eventsHandler.handleEventSubProcess(processDefinition, sProcessInstance);
            } catch (final SBonitaException e) {
                throw new SActivityExecutionException("Unable to register events for event sub process in process " + processDefinition.getName() + " "
                        + processDefinition.getVersion(), e);
            }
        }
    }

    @Override
    public SProcessInstance startElements(final SProcessDefinition sDefinition, final SProcessInstance sProcessInstance)
            throws SProcessInstanceCreationException, SFlowNodeExecutionException {
        return startElements(sDefinition, sProcessInstance, -1, -1);
    }

    @Override
    public SProcessInstance startElements(final SProcessDefinition sDefinition, final SProcessInstance sProcessInstance, final long subProcessDefinitionId,
            final long targetSFlowNodeDefinitionId) throws SProcessInstanceCreationException, SFlowNodeExecutionException {
        List<SFlowNodeInstance> flowNodeInstances = initializeFirstExecutableElements(sProcessInstance, sDefinition, subProcessDefinitionId,
                targetSFlowNodeDefinitionId);
        // process is initialized and now the engine trigger jobs to execute other activities, give the hand back
        ProcessInstanceState state;
        final int size = flowNodeInstances.size();
        if (size == 0) {
            state = ProcessInstanceState.COMPLETED;
        } else {
            state = ProcessInstanceState.STARTED;
        }
        try {
            tokenService.createTokens(sProcessInstance.getId(), sProcessInstance.getProcessDefinitionId(), null, size);
            processInstanceService.setState(sProcessInstance, state);
        } catch (final SBonitaException e) {
            throw new SProcessInstanceCreationException("Unable to set the state on the process", e);
        }
        for (final SFlowNodeInstance flowNode : flowNodeInstances) {
            try {
                workService.registerWork(WorkFactory.createExecuteFlowNodeWork(flowNode.getId(), null, null, sProcessInstance.getId()));
            } catch (final WorkRegisterException e) {
                throw new SFlowNodeExecutionException("unable to trigger execution of flow node " + flowNode, e);
            }
        }
        return sProcessInstance;
    }

    @Override
    public String getHandledType() {
        return SFlowElementsContainerType.PROCESS.name();
    }

    private void createAndExecuteActivities(final Long processDefinitionId, final SFlowNodeInstance flowNodeInstance, final long parentProcessInstanceId,
            final List<SFlowNodeDefinition> choosenActivityDefinitions, final long rootProcessInstanceId, final Long tokenRefId) throws SBonitaException {
        final SProcessInstance parentProcessInstance = processInstanceService.getProcessInstance(parentProcessInstanceId);
        final SStateCategory stateCategory = parentProcessInstance.getStateCategory();

        // Create Activities
        final List<SFlowNodeInstance> sFlowNodeInstances = bpmInstancesCreator.createFlowNodeInstances(processDefinitionId,
                flowNodeInstance.getRootContainerId(), flowNodeInstance.getParentContainerId(), choosenActivityDefinitions, rootProcessInstanceId,
                parentProcessInstanceId, stateCategory, tokenRefId);

        // Execute Activities
        for (final SFlowNodeInstance sFlowNodeInstance : sFlowNodeInstances) {
            workService.registerWork(WorkFactory.createExecuteFlowNodeWork(sFlowNodeInstance.getId(), null, null, parentProcessInstanceId));
        }
    }
}
