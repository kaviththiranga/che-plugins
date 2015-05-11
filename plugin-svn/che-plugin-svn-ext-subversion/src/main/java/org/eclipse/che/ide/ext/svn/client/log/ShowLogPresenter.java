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
package org.eclipse.che.ide.ext.svn.client.log;

import org.eclipse.che.ide.api.parts.ProjectExplorerPart;
import org.eclipse.che.ide.ext.svn.client.SubversionClientService;
import org.eclipse.che.ide.ext.svn.client.common.RawOutputPresenter;
import org.eclipse.che.ide.ext.svn.client.common.SubversionActionPresenter;
import org.eclipse.che.ide.ext.svn.shared.CLIOutputResponse;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.ext.svn.shared.InfoResponse;
import org.eclipse.che.ide.ext.svn.shared.SubversionItem;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Manages the displaying commit log messages for specified period.
 */
public class ShowLogPresenter extends SubversionActionPresenter {

    private final AppContext              appContext;
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private final SubversionClientService subversionClientService;
    private final NotificationManager     notificationManager;

    private final ShowLogsView            view;

    /**
     * Creates an instance of this presenter.
     */
    @Inject
    protected ShowLogPresenter(final AppContext appContext,
                               final DtoUnmarshallerFactory dtoUnmarshallerFactory,
                               final EventBus eventBus,
                               final WorkspaceAgent workspaceAgent,
                               final RawOutputPresenter console,
                               final SubversionClientService subversionClientService,
                               final NotificationManager notificationManager,
                               final ProjectExplorerPart projectExplorerPart,
                               final ShowLogsView view) {
        super(appContext, eventBus, console, workspaceAgent, projectExplorerPart);

        this.appContext = appContext;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.subversionClientService = subversionClientService;
        this.notificationManager = notificationManager;
        this.view = view;

        view.setDelegate(new ShowLogsView.Delegate() {
            @Override
            public void logClicked() {
                String range = view.rangeFiend().getValue();
                if (range != null && !range.trim().isEmpty()) {
                    view.hide();
                    showLogs(range);
                }
            }

            @Override
            public void cancelClicked() {
                view.hide();
            }
        });
    }

    /**
     * Fetches the count of revisions and opens the popup.
     */
    public void showLog() {
        if (appContext.getCurrentProject() == null) {
            return;
        }

        subversionClientService.info(appContext.getCurrentProject().getRootProject().getPath(), getSelectedPaths().get(0), "HEAD", false,
                new AsyncRequestCallback<InfoResponse>(dtoUnmarshallerFactory.newUnmarshaller(InfoResponse.class)) {
                    @Override
                    protected void onSuccess(InfoResponse result) {
                        if (result.getErrorOutput() != null && !result.getErrorOutput().isEmpty()) {
                            printResponse(null, null, result.getErrorOutput());
                            notificationManager.showError("Unable to execute subversion command");
                            return;
                        }

                        SubversionItem subversionItem = result.getItems().get(0);
                        view.setRevisionCount(subversionItem.getRevision());
                        view.rangeFiend().setValue("1:" + subversionItem.getRevision());
                        view.show();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showError(exception.getMessage());
                    }
                });

    }

    /**
     * Fetches and displays commit log messages for specified range.
     *
     * @param range range to be logged
     */
    private void showLogs(String range) {
        subversionClientService.showLog(appContext.getCurrentProject().getRootProject().getPath(), getSelectedPaths(), range,
                new AsyncRequestCallback<CLIOutputResponse>(dtoUnmarshallerFactory.newUnmarshaller(CLIOutputResponse.class)) {
                    @Override
                    protected void onSuccess(CLIOutputResponse result) {
                        printCommand(result.getCommand());
                        printAndSpace(result.getOutput());
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showError(exception.getMessage());
                    }
                });
    }

}
