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
package org.eclipse.che.builder.maven;

import org.eclipse.che.api.builder.internal.Builder;
import org.eclipse.che.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/** @author andrew00x */
@DynaModule
public class MavenBuilderModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Builder> multiBinder = Multibinder.newSetBinder(binder(), Builder.class);
        multiBinder.addBinding().to(MavenBuilder.class);
    }
}
