/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.eclipse.che.plugin.docker.machine;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.notification.EventSubscriber;
import org.eclipse.che.api.machine.server.MachineException;
import org.eclipse.che.api.machine.server.MachineImpl;
import org.eclipse.che.api.machine.server.MachineManager;
import org.eclipse.che.api.machine.shared.dto.MachineStateEvent;
import org.eclipse.che.plugin.docker.client.DockerConnector;
import org.eclipse.che.plugin.docker.client.Exec;
import org.eclipse.che.plugin.docker.client.LogMessage;
import org.eclipse.che.plugin.docker.client.LogMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Set;

/**
 * @author Alexander Garagatyi
 */
@Singleton // must be eager
public class DockerMachineTerminal {
    private static final Logger LOG = LoggerFactory.getLogger(DockerMachineTerminal.class);

    private final EventService    eventService;
    private final DockerConnector docker;
    private final MachineManager  machineManager;
    private final Set<String>     systemExposedPorts;
    private final Set<String>     systemVolumes;

    @Inject
    public DockerMachineTerminal(EventService eventService,
                                 DockerConnector docker,
                                 MachineManager machineManager,
                                 @Named("machine.docker.system_exposed_ports") Set<String> systemExposedPorts,
                                 @Named("machine.docker.system_volumes") Set<String> systemVolumes) {
        this.eventService = eventService;
        this.docker = docker;
        this.machineManager = machineManager;
        this.systemExposedPorts = systemExposedPorts;
        this.systemVolumes = systemVolumes;
    }

    @PostConstruct
    private void start() {
        systemExposedPorts.add("4300");
        systemVolumes.add("/usr/local/codenvy/terminal:/usr/local/codenvy/terminal");//TODO add :ro

        eventService.subscribe(new EventSubscriber<MachineStateEvent>() {
            @Override
            public void onEvent(MachineStateEvent event) {
                try {
                    final MachineImpl machine = machineManager.getMachine(event.getMachineId());
                    final String containerId = machine.getMetadata().getProperties().get("id");

                    final Exec exec = docker.createExec(containerId, true, "/bin/bash", "-c",
                                                        "/usr/local/codenvy/terminal/terminal -addr :4300 -cmd /bin/sh -static /usr/local/codenvy/terminal/");
                    docker.startExec(exec.getId(), new LogMessageProcessor() {
                        @Override
                        public void process(LogMessage logMessage) {
                            LOG.error(String.format("Terminal error in container %s. %s", containerId, logMessage.getContent()));
                        }
                    });
                } catch (IOException | MachineException | NotFoundException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        });
    }
}
