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
package org.eclipse.che.ide.ext.runner.client.manager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.inject.factories.WidgetFactory;
import org.eclipse.che.ide.ext.runner.client.manager.button.ButtonWidget;
import org.eclipse.che.ide.ext.runner.client.manager.info.MoreInfo;
import org.eclipse.che.ide.ext.runner.client.manager.menu.MenuWidget;
import org.eclipse.che.ide.ext.runner.client.manager.menu.entry.MenuEntry;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.tabs.container.TabContainer;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class provides view representation of runner panel.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class RunnerManagerViewImpl extends BaseView<RunnerManagerView.ActionDelegate> implements RunnerManagerView {
    interface RunnerManagerViewImplUiBinder extends UiBinder<Widget, RunnerManagerViewImpl> {
    }

    private static final RunnerManagerViewImplUiBinder UI_BINDER = GWT.create(RunnerManagerViewImplUiBinder.class);

    private static final String GWT_POPUP_STANDARD_STYLE = "gwt-PopupPanel";
    private static final String SPLITTER_STYLE_NAME      = "gwt-SplitLayoutPanel-HDragger";

    private static final int SHIFT_LEFT = 80;
    private static final int SHIFT_TOP  = 130;

    private static final int MENU_SHIFT_LEFT = -120;
    private static final int MENU_SHIFT_TOP  = 20;

    private static final int MAIN_SPLITTER_WIDTH       = 2;
    private static final int PROPERTIES_SPLITTER_WIDTH = 5;

    @UiField(provided = true)
    SplitLayoutPanel mainPanel;
    @UiField(provided = true)
    SplitLayoutPanel propertiesPanel;

    @UiField
    SimplePanel leftTabsPanel;

    @UiField
    FlowPanel   otherButtonsPanel;
    @UiField
    FlowPanel   runButtonPanel;
    @UiField
    SimplePanel rightPropertiesPanel;
    @UiField
    SimplePanel leftPropertiesPanel;

    //info panel
    @UiField
    Label             appReference;
    @UiField
    FlowPanel         moreInfoPanel;
    @UiField
    Label             timeout;
    @UiField
    SimpleLayoutPanel image;
    @UiField
    FlowPanel         debugPanel;
    @UiField
    Label             debugPort;

    @UiField(provided = true)
    final RunnerResources            resources;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;

    private final WidgetFactory widgetFactory;
    private final AppContext    appContext;
    private final PopupPanel    moreInfoPopup;
    private final MoreInfo      moreInfoWidget;

    private ButtonWidget run;
    private ButtonWidget reRun;
    private ButtonWidget stop;
    private ButtonWidget logs;

    private String url;
    private Widget splitter;
    private int    panelWidth;

    @Inject
    public RunnerManagerViewImpl(PartStackUIResources partStackUIResources,
                                 RunnerResources resources,
                                 RunnerLocalizationConstant locale,
                                 WidgetFactory widgetFactory,
                                 AppContext appContext,
                                 PopupPanel moreInfoPopup,
                                 final PopupPanel menuPopup) {
        super(partStackUIResources);

        this.appContext = appContext;
        this.resources = resources;
        this.locale = locale;
        this.widgetFactory = widgetFactory;
        this.moreInfoWidget = widgetFactory.createMoreInfo();
        this.panelWidth = 800;

        this.mainPanel = new SplitLayoutPanel(MAIN_SPLITTER_WIDTH);
        this.propertiesPanel = new SplitLayoutPanel(PROPERTIES_SPLITTER_WIDTH);

        titleLabel.setText(locale.runnersPanelTitle());
        setContentWidget(UI_BINDER.createAndBindUi(this));

        this.mainPanel.setWidgetMinSize(leftTabsPanel, 185);
        this.debugPanel.setVisible(false);

        this.moreInfoPopup = moreInfoPopup;
        this.moreInfoPopup.removeStyleName(GWT_POPUP_STANDARD_STYLE);
        this.moreInfoPopup.add(moreInfoWidget);

        SVGImage icon = new SVGImage(resources.moreInfo());
        icon.getElement().setAttribute("class", resources.runnerCss().mainButtonIcon());
        image.getElement().setInnerHTML(icon.toString());

        addMoreInfoPanelHandler();

        changeSplitterStyle(mainPanel, resources.runnerCss().splitter());
        changeSplitterStyle(propertiesPanel, resources.runnerCss().propertiesSplitter());

        initializeButtons();

        initializeMenu(menuPopup);
    }

    private void addMoreInfoPanelHandler() {
        moreInfoPanel.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                image.addStyleName(resources.runnerCss().opacityButton());

                delegate.onMoreInfoBtnMouseOver();
            }
        }, MouseOverEvent.getType());

        moreInfoPanel.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                image.removeStyleName(resources.runnerCss().opacityButton());

                moreInfoPopup.hide();
            }
        }, MouseOutEvent.getType());
    }

    private void changeSplitterStyle(@Nonnull SplitLayoutPanel panel, @Nonnull String style) {
        int widgetCount = panel.getWidgetCount();

        for (int i = 0; i < widgetCount; i++) {
            Widget widget = panel.getWidget(i);
            String styleName = widget.getStyleName();

            if (SPLITTER_STYLE_NAME.equals(styleName)) {
                this.splitter = widget;

                widget.removeStyleName(styleName);
                widget.addStyleName(style);
            }
        }
    }

    private void initializeButtons() {
        ButtonWidget.ActionDelegate runDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onRunButtonClicked();
            }
        };
        run = createButton(resources.run(), locale.tooltipRunButton(), runDelegate, runButtonPanel);
        if (appContext.getCurrentProject() != null) {
            run.setEnable();
        }

        ButtonWidget.ActionDelegate reRunDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onRerunButtonClicked();
            }
        };
        reRun = createButton(resources.reRun(), locale.tooltipRerunButton(), reRunDelegate, otherButtonsPanel);

        ButtonWidget.ActionDelegate stopDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onStopButtonClicked();
            }
        };
        stop = createButton(resources.stop(), locale.tooltipStopButton(), stopDelegate, otherButtonsPanel);

        ButtonWidget.ActionDelegate logsDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onLogsButtonClicked();
            }
        };
        logs = createButton(resources.logs(), locale.tooltipLogsButton(), logsDelegate, otherButtonsPanel);
    }

    private void initializeMenu(@Nonnull final PopupPanel menuPopup) {
        menuPopup.removeStyleName(GWT_POPUP_STANDARD_STYLE);

        MenuWidget menuWidget = widgetFactory.createMenuWidget();
        MenuEntry entry = widgetFactory.createMenuEntry(locale.menuToggleSplitter());

        entry.setDelegate(new MenuEntry.ActionDelegate() {
            @Override
            public void onEntryClicked(boolean isSplitterShow) {
                delegate.onToggleSplitterClicked(isSplitterShow);

                menuPopup.hide();
            }
        });

        menuWidget.addEntry(entry);

        menuPopup.add(menuWidget);

        ButtonWidget headerMenuBtn = widgetFactory.createButton(locale.tooltipHeaderMenuButton(), resources.menuIcon());
        headerMenuBtn.setEnable();
        headerMenuBtn.setDelegate(new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                int x = menuPanel.getAbsoluteLeft() + MENU_SHIFT_LEFT;
                int y = menuPanel.getAbsoluteTop() + MENU_SHIFT_TOP;

                menuPopup.setPopupPosition(x, y);

                menuPopup.show();
            }
        });

        addMenuButton(headerMenuBtn);

        menuWidget.getSpan().addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                menuPopup.hide();
            }
        }, ClickEvent.getType());
    }

    @Nonnull
    private ButtonWidget createButton(@Nonnull SVGResource icon,
                                      @Nonnull String prompt,
                                      @Nonnull ButtonWidget.ActionDelegate delegate,
                                      @Nonnull FlowPanel buttonPanel) {
        ButtonWidget button = widgetFactory.createButton(prompt, icon);
        button.setDelegate(delegate);
        button.setDisable();

        buttonPanel.add(button);

        return button;
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        changeButtonsState(runner);

        moreInfoWidget.update(runner);
        debugPanel.setVisible(false);
    }

    private void changeButtonsState(@Nonnull Runner runner) {
        if (appContext.getCurrentProject() == null) {
            run.setDisable();
            stop.setDisable();
            reRun.setDisable();
            logs.setDisable();
            return;
        }

        run.setEnable();
        reRun.setDisable();
        stop.setEnable();
        logs.setEnable();

        switch (runner.getStatus()) {
            case IN_QUEUE:
                stop.setDisable();
                logs.setDisable();
                break;
            case FAILED:
                stop.setDisable();
                reRun.setEnable();
                logs.setDisable();
                break;
            case STOPPED:
                stop.setDisable();
                reRun.setEnable();
                logs.setDisable();
                break;
            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationURl(@Nullable String applicationUrl) {
        url = null;
        appReference.removeStyleName(resources.runnerCss().cursor());

        if (applicationUrl != null && applicationUrl.startsWith("http")) {
            url = applicationUrl;
            appReference.addStyleName(resources.runnerCss().cursor());
        }

        appReference.setText(applicationUrl);
    }

    @Override
    public void setDebugPort(@Nullable String port) {
        boolean visible = port != null && !port.isEmpty();
        debugPanel.setVisible(visible);

        if (visible) {
            debugPort.setText(port);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setTimeout(@Nonnull String timeoutValue) {
        timeout.setText(timeoutValue);
    }

    /** {@inheritDoc} */
    @Override
    public void showMoreInfoPopup(@Nullable Runner runner) {
        moreInfoWidget.update(runner);

        int x = timeout.getAbsoluteLeft() - SHIFT_LEFT;
        int y = timeout.getAbsoluteTop() - SHIFT_TOP;

        moreInfoPopup.setPopupPosition(x, y);
        moreInfoPopup.show();
    }

    /** {@inheritDoc} */
    @Override
    public void updateMoreInfoPopup(@Nonnull Runner runner) {
        moreInfoWidget.update(runner);
    }

    /** {@inheritDoc} */
    @Override
    public void setLeftPanel(@Nonnull TabContainer containerPresenter) {
        containerPresenter.go(leftTabsPanel);
    }

    /** {@inheritDoc} */
    @Override
    public void setRightPropertiesPanel(@Nonnull TabContainer containerPresenter) {
        containerPresenter.showTabTitle(locale.runnerTabConsole(), false);
        containerPresenter.showTabTitle(locale.runnerTabProperties(), false);

        containerPresenter.showTab(locale.runnerTabTerminal());

        containerPresenter.go(rightPropertiesPanel);
    }

    /** {@inheritDoc} */
    @Override
    public void setLeftPropertiesPanel(@Nonnull TabContainer containerPresenter) {
        containerPresenter.showTab(locale.runnerTabConsole());

        leftPropertiesPanel.getElement().getParentElement().getStyle().setDisplay(Display.BLOCK);
        propertiesPanel.getElement().getFirstChildElement().getNextSiblingElement().getStyle().setWidth(panelWidth, Unit.PX);

        splitter.removeStyleName(resources.runnerCss().hideSplitter());

        containerPresenter.go(leftPropertiesPanel);
    }

    /** {@inheritDoc} */
    @Override
    public void setGeneralPropertiesPanel(@Nonnull TabContainer containerPresenter) {
        containerPresenter.showTabTitle(locale.runnerTabConsole(), true);
        containerPresenter.showTabTitle(locale.runnerTabProperties(), true);
        containerPresenter.showTab(locale.runnerTabTerminal());

        panelWidth = rightPropertiesPanel.getElement().getParentElement().getScrollWidth();

        leftPropertiesPanel.getElement().getParentElement().getStyle().setDisplay(Display.NONE);
        propertiesPanel.getElement().getFirstChildElement().getNextSiblingElement().getStyle().setWidth(100.1, Unit.PC);

        splitter.addStyleName(resources.runnerCss().hideSplitter());

        containerPresenter.go(rightPropertiesPanel);
    }

    /** {@inheritDoc} */
    @Override
    public void hideOtherButtons() {
        otherButtonsPanel.setVisible(false);
    }

    /** {@inheritDoc} */
    @Override
    public void showOtherButtons() {
        otherButtonsPanel.setVisible(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableRunButton(boolean isEnable) {
        if (isEnable) {
            run.setEnable();
        } else {
            run.setDisable();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableReRunButton(boolean isEnable) {
        if (isEnable) {
            reRun.setEnable();
        } else {
            reRun.setDisable();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableStopButton(boolean isEnable) {
        if (isEnable) {
            stop.setEnable();
        } else {
            stop.setDisable();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableLogsButton(boolean isEnable) {
        if (isEnable) {
            logs.setEnable();
        } else {
            logs.setDisable();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void showLog(@Nonnull String url) {
        Window.open(url, "_blank", "");
    }

    @UiHandler("appReference")
    public void onAppReferenceClicked(@SuppressWarnings("UnusedParameters") ClickEvent clickEvent) {
        if (url != null) {
            Window.open(url, "_blank", "");
        }
    }
}