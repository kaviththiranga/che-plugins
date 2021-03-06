/*******************************************************************************
 * Copyright (c) 2014-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.editor.codemirrorjso.client;

import com.google.gwt.core.client.JavaScriptObject;

public class CMSelectionOverlay extends JavaScriptObject {

    protected CMSelectionOverlay() {
    }

    public final native CMPositionOverlay getAnchor() /*-{
        return this.anchor;
    }-*/;

    public final native CMPositionOverlay getHead() /*-{
        return this.head;
    }-*/;

    public static final native CMSelectionOverlay create(CMPositionOverlay anchor, CMPositionOverlay head) /*-{
        var result = {};
        result.anchor = anchor;
        result.head = head;
        return result;
    }-*/;
}
