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

import org.bonitasoft.engine.bpm.document.Document;
import org.bonitasoft.engine.bpm.document.impl.DocumentImpl;

/**
 * @author Nicolas Chabanoles
 */
public class DocumentBuilder {

    private DocumentImpl document;

    public DocumentBuilder createNewInstance(final String documentName, final boolean hasContent) {
        document = new DocumentImpl();
        document.setName(documentName);
        document.setHasContent(hasContent);
        return this;
    }

    public Document done() {
        return document;
    }

    public DocumentBuilder setFileName(final String fileName) {
        document.setFileName(fileName);
        return this;
    }

    public DocumentBuilder setURL(final String documentUrl) {
        document.setUrl(documentUrl);
        return this;
    }

    public DocumentBuilder setContentMimeType(final String documentContentMimeType) {
        document.setContentMimeType(documentContentMimeType);
        return this;
    }

}
