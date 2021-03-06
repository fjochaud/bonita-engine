/**
 * Copyright (C) 2012 BonitaSoft S.A.
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
package org.bonitasoft.engine.work;

import java.util.concurrent.TimeoutException;

import org.bonitasoft.engine.commons.ServiceWithLifecycle;

/**
 * This service allows to trigger the execution of work asynchronously
 * Any runnable registered on the service will be launched in other thread at the end of the transaction.
 * 
 * @author Charles Souillard
 * @author Baptiste Mesta
 */
public interface WorkService extends ServiceWithLifecycle {

    /**
     * This operation MUST be called with an active transaction. If no active transaction is found, a WorkRegisterException is thrown
     * @param work
     * @throws WorkRegisterException
     * @since 6.0
     */
    void registerWork(final BonitaWork work) throws WorkRegisterException;

    /**
     * @param work
     * @throws WorkRegisterException
     * @since 6.0
     */
    void executeWork(final BonitaWork work) throws WorkRegisterException;

    /**
     * 
     * Stop the execution of works for a tenant
     * 
     * @param tenantId
     */
    void stop(Long tenantId);

    /**
     * 
     * Allow to start works of this tenant
     * 
     * @param tenantId
     */
    void start(Long tenantId);

    /**
     * 
     * Stop the execution of work for this local work service
     * 
     * @throws TimeoutException
     */
    void stop() throws TimeoutException;

    /**
     * 
     * start the execution of work for this local work service
     * 
     * @throws TimeoutException
     */
    void start();

}
