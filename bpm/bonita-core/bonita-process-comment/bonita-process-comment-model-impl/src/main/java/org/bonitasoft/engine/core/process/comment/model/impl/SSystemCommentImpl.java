/**
 * Copyright (C) 2012-2013 BonitaSoft S.A.
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
package org.bonitasoft.engine.core.process.comment.model.impl;

import org.bonitasoft.engine.core.process.comment.model.SSystemComment;

/**
 * @author Hongwen Zang
 * @author Matthieu Chaffotte
 */
public class SSystemCommentImpl extends SCommentImpl implements SSystemComment {

    private static final long serialVersionUID = -8294077176495786761L;

    public SSystemCommentImpl() {
        super();
    }

    public SSystemCommentImpl(final long processInstanceId, final String content) {
        super(processInstanceId, content);
    }

}
