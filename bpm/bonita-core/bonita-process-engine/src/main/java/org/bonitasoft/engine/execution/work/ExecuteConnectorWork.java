/**
 * Copyright (C) 2013 BonitaSoft S.A.
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
package org.bonitasoft.engine.execution.work;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.core.connector.ConnectorInstanceService;
import org.bonitasoft.engine.core.connector.ConnectorResult;
import org.bonitasoft.engine.core.connector.ConnectorService;
import org.bonitasoft.engine.core.expression.control.model.SExpressionContext;
import org.bonitasoft.engine.core.operation.model.SOperation;
import org.bonitasoft.engine.core.process.definition.ProcessDefinitionService;
import org.bonitasoft.engine.core.process.definition.model.SConnectorDefinition;
import org.bonitasoft.engine.core.process.definition.model.event.SEndEventDefinition;
import org.bonitasoft.engine.core.process.instance.model.SConnectorInstance;
import org.bonitasoft.engine.core.process.instance.model.SConnectorInstanceWithFailureInfo;
import org.bonitasoft.engine.core.process.instance.model.event.SThrowEventInstance;
import org.bonitasoft.engine.log.technical.TechnicalLogSeverity;
import org.bonitasoft.engine.service.TenantServiceAccessor;
import org.bonitasoft.engine.transaction.TransactionService;
import org.bonitasoft.engine.work.WorkService;

/**
 * @author Baptiste Mesta
 * @author Matthieu Chaffotte
 * @author Celine Souchet
 */
public abstract class ExecuteConnectorWork extends TenantAwareBonitaWork {

    private static final long serialVersionUID = 9031279948838300081L;

    protected final long processDefinitionId;

    protected final long connectorInstanceId;

    protected final String connectorDefinitionName;

    private final SExpressionContext inputParametersContext;

    public ExecuteConnectorWork(final long processDefinitionId, final long connectorInstanceId, final String connectorDefinitionName,
            final SExpressionContext inputParametersContext) {
        super();
        this.processDefinitionId = processDefinitionId;
        this.connectorInstanceId = connectorInstanceId;
        this.connectorDefinitionName = connectorDefinitionName;
        this.inputParametersContext = inputParametersContext;
    }

    protected abstract void errorEventOnFail(Map<String, Object> context, SConnectorDefinition sConnectorDefinition, Throwable throwable)
            throws SBonitaException;

    protected abstract SThrowEventInstance createThrowErrorEventInstance(Map<String, Object> context, final SEndEventDefinition eventDefinition)
            throws SBonitaException;

    protected abstract SConnectorDefinition getSConnectorDefinition(final ProcessDefinitionService processDefinitionService) throws SBonitaException;

    protected abstract void setContainerInFail(Map<String, Object> context) throws SBonitaException;

    protected abstract void continueFlow(Map<String, Object> context) throws SBonitaException;

    protected abstract void evaluateOutput(Map<String, Object> context, final ConnectorResult result, SConnectorDefinition sConnectorDefinition)
            throws SBonitaException;

    protected ClassLoader getClassLoader(final Map<String, Object> context) throws SBonitaException {
        return getTenantAccessor(context).getClassLoaderService().getLocalClassLoader("process", processDefinitionId);
    }

    protected void setConnectorAndContainerToFailed(final Map<String, Object> context, final Throwable throwable) throws SBonitaException {
        setConnectorOnlyToFailed(context, throwable);
        setContainerInFail(context);
    }

    protected void setConnectorOnlyToFailed(final Map<String, Object> context, final Throwable throwable) throws SBonitaException {
        final ConnectorInstanceService connectorInstanceService = getTenantAccessor(context).getConnectorInstanceService();
        final SConnectorInstanceWithFailureInfo connectorInstanceWithFailure = connectorInstanceService
                .getConnectorInstanceWithFailureInfo(connectorInstanceId);
        connectorInstanceService.setState(connectorInstanceWithFailure, ConnectorService.FAILED);
        connectorInstanceService.setConnectorInstanceFailureException(connectorInstanceWithFailure, throwable);
    }

    protected void evaluateOutput(final Map<String, Object> context, final ConnectorResult result, final SConnectorDefinition sConnectorDefinition,
            final Long id, final String containerType) throws SBonitaException {
        final TenantServiceAccessor tenantAccessor = getTenantAccessor(context);
        final ConnectorInstanceService connectorInstanceService = tenantAccessor.getConnectorInstanceService();
        final ConnectorService connectorService = tenantAccessor.getConnectorService();
        final List<SOperation> outputs = sConnectorDefinition.getOutputs();
        final SExpressionContext sExpressionContext = new SExpressionContext(id, containerType, processDefinitionId);
        connectorService.executeOutputOperation(outputs, sExpressionContext, result);
        connectorInstanceService.setState(connectorInstanceService.getConnectorInstance(connectorInstanceId), ConnectorService.DONE);
    }

    @Override
    public void work(final Map<String, Object> context) throws Exception {
        final TenantServiceAccessor tenantAccessor = getTenantAccessor(context);
        final ConnectorService connectorService = tenantAccessor.getConnectorService();
        final ConnectorInstanceService connectorInstanceService = tenantAccessor.getConnectorInstanceService();
        final TransactionService transactionService = tenantAccessor.getTransactionService();
        final ProcessDefinitionService processDefinitionService = tenantAccessor.getProcessDefinitionService();
        final ClassLoader processClassloader = getClassLoader(context);
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(processClassloader);
            final EvaluateParameterAndGetConnectorInstance callable = new EvaluateParameterAndGetConnectorInstance(connectorService, processDefinitionService,
                    connectorInstanceService);
            transactionService.executeInTransaction(callable);
            final SConnectorDefinition sConnectorDefinition = callable.getsConnectorDefinition();
            final SConnectorInstance connectorInstance = callable.getConnectorInstance();
            final ConnectorResult result = connectorService.executeConnector(processDefinitionId, connectorInstance, processClassloader,
                    callable.getInputParameters());
            // evaluate output and trigger the execution of the flow node
            final WorkService workService = tenantAccessor.getWorkService();
            transactionService.executeInTransaction(new EvaluateConnectorOutputsTxContent(result, sConnectorDefinition, workService, context));
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    @Override
    public void handleFailure(final Throwable e, final Map<String, Object> context) throws Exception {
        final TransactionService transactionService = getTenantAccessor(context).getTransactionService();
        final ProcessDefinitionService processDefinitionService = getTenantAccessor(context).getProcessDefinitionService();
        if (handleError(context, transactionService, processDefinitionService, e)) {
            transactionService.executeInTransaction(new ContinueFlowTxContent(context));
        }
    }

    private boolean handleError(final Map<String, Object> context, final TransactionService transactionService,
            final ProcessDefinitionService processDefinitionService, final Throwable e) throws Exception {
        final HandleConnectorOnFailEventTxContent handleError = new HandleConnectorOnFailEventTxContent(e, processDefinitionService, context);
        return transactionService.executeInTransaction(handleError);
    }

    private final class EvaluateParameterAndGetConnectorInstance implements Callable<Void> {

        private final ConnectorService connectorService;

        private final ConnectorInstanceService connectorInstanceService;

        private Map<String, Object> inputParameters;

        private SConnectorInstance connectorInstance;

        private final ProcessDefinitionService processDefinitionService;

        private SConnectorDefinition sConnectorDefinition;

        private EvaluateParameterAndGetConnectorInstance(final ConnectorService connectorService, final ProcessDefinitionService processDefinitionService,
                final ConnectorInstanceService connectorInstanceService) {
            this.connectorService = connectorService;
            this.processDefinitionService = processDefinitionService;
            this.connectorInstanceService = connectorInstanceService;
        }

        public Map<String, Object> getInputParameters() {
            return inputParameters;
        }

        public SConnectorInstance getConnectorInstance() {
            return connectorInstance;
        }

        public SConnectorDefinition getsConnectorDefinition() {
            return sConnectorDefinition;
        }

        @Override
        public Void call() throws Exception {
            sConnectorDefinition = getSConnectorDefinition(processDefinitionService);
            inputParameters = connectorService.evaluateInputParameters(sConnectorDefinition.getInputs(), inputParametersContext, null);
            connectorInstance = connectorInstanceService.getConnectorInstance(connectorInstanceId);
            return null;
        }
    }

    /**
     * @author Emmanuel Duchastenier
     */
    private final class HandleConnectorOnFailEventTxContent implements Callable<Boolean> {

        private final Throwable e;

        private final ProcessDefinitionService processDefinitionService;

        private final Map<String, Object> context;

        private HandleConnectorOnFailEventTxContent(final Throwable e, final ProcessDefinitionService processDefinitionService,
                final Map<String, Object> context) {
            this.e = e;
            this.processDefinitionService = processDefinitionService;
            this.context = context;

        }

        @Override
        public Boolean call() throws Exception {
            final TenantServiceAccessor tenantAccessor = getTenantAccessor(context);
            tenantAccessor.getTechnicalLoggerService().log(getClass(), TechnicalLogSeverity.WARNING,
                    "Error while executing connector with id " + connectorInstanceId, e);
            final SConnectorDefinition sConnectorDefinition = getSConnectorDefinition(processDefinitionService);
            switch (sConnectorDefinition.getFailAction()) {
                case ERROR_EVENT:
                    errorEventOnFail(context, sConnectorDefinition, e);
                    return false;
                case FAIL:
                    setConnectorAndContainerToFailed(context, e);
                    return false;
                case IGNORE:
                    setConnectorOnlyToFailed(context, e);
                    return true;
                default:
                    throw new Exception("No action defined for " + sConnectorDefinition.getFailAction());
            }
        }
    }

    /**
     * @author Emmanuel Duchastenier
     */
    private final class ContinueFlowTxContent implements Callable<Void> {

        private final Map<String, Object> context;

        public ContinueFlowTxContent(final Map<String, Object> context) {
            this.context = context;
        }

        @Override
        public Void call() throws Exception {
            continueFlow(context);
            return null;
        }
    }

    /**
     * @author Emmanuel Duchastenier
     */
    private final class EvaluateConnectorOutputsTxContent implements Callable<Void> {

        private final ConnectorResult result;

        private final SConnectorDefinition sConnectorDefinition;

        private final Map<String, Object> context;

        private EvaluateConnectorOutputsTxContent(final ConnectorResult result, final SConnectorDefinition sConnectorDefinition, final WorkService workService,
                final Map<String, Object> context) {
            this.result = result;
            this.sConnectorDefinition = sConnectorDefinition;
            this.context = context;
        }

        @Override
        public Void call() throws Exception {
            evaluateOutput(context, result, sConnectorDefinition);
            continueFlow(context);
            return null;
        }
    }

}
