/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.svn.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that provides methods for parsing CLI output.
 */
public final class CLIOutputParser {

    public static List<StatusItem> parseFilesStatus(final List<String> statusOutput) {
        final List<StatusItem> statusItems = new ArrayList<>();

        for (final String line : statusOutput) {
            statusItems.add(new StatusItem(line));
        }

        return statusItems;
    }

}
