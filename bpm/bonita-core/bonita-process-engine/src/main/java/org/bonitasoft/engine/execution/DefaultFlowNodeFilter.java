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
package org.bonitasoft.engine.execution;

import org.bonitasoft.engine.core.process.definition.model.SFlowNodeDefinition;
import org.bonitasoft.engine.core.process.definition.model.SFlowNodeType;
import org.bonitasoft.engine.core.process.definition.model.SSubProcessDefinition;
import org.bonitasoft.engine.core.process.definition.model.event.SStartEventDefinition;


/**
 * @author Elias Ricken de Medeiros
 *
 */
public class DefaultFlowNodeFilter implements Filter<SFlowNodeDefinition>{
    
    private long targetSFlowNodeDefinitionId;

    public DefaultFlowNodeFilter(final long targetSFlowNodeDefinitionId) {
        this.targetSFlowNodeDefinitionId = targetSFlowNodeDefinitionId;
    }

    @Override
    public boolean select(SFlowNodeDefinition flowNode) {
        if (targetSFlowNodeDefinitionId != -1) {
            // When call start by event triggers, run only the target of trigger
            // The starterId equals 0, when start process in work (See InstantiateProcessWork)
            return flowNode.getId() == targetSFlowNodeDefinitionId;
        }
        // Not started by an event: start all elements that are start point and are not triggered by an event
        return flowNode.getIncomingTransitions().isEmpty()
                && !(SFlowNodeType.SUB_PROCESS.equals(flowNode.getType()) && ((SSubProcessDefinition) flowNode).isTriggeredByEvent())
                && !SFlowNodeType.BOUNDARY_EVENT.equals(flowNode.getType())
                // is not a start event or if it is, is not triggered by an event
                && (!SFlowNodeType.START_EVENT.equals(flowNode.getType()) || ((SStartEventDefinition) flowNode).getEventTriggers()
                        .isEmpty());
    }

}