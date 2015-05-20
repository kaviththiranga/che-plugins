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
package org.eclipse.che.plugin.docker.machine;

import org.eclipse.che.api.machine.server.spi.InstanceSnapshotKey;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.dto.shared.JsonStringMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Set of keys that identifies docker image properties
 *
 * @author andrew00x
 */
public class DockerInstanceSnapshotKey implements InstanceSnapshotKey {
    public static final String REPOSITORY = "repository";
    public static final String TAG        = "tag";
    public static final String ID         = "id";
    public static final String REGISTRY   = "registry";

    private final Map<String, String> fields;

    public DockerInstanceSnapshotKey(InstanceSnapshotKey instanceSnapshotKey) {
        fields = new HashMap<>(4);
        fields.put(REPOSITORY, instanceSnapshotKey.getFields().get(REPOSITORY));
        fields.put(TAG, instanceSnapshotKey.getFields().get(TAG));
        fields.put(ID, instanceSnapshotKey.getFields().get(ID));
        fields.put(REGISTRY, instanceSnapshotKey.getFields().get(REGISTRY));
    }

    public DockerInstanceSnapshotKey(String repository, String tag, String id, String registry) {
        fields = new HashMap<>(4);
        fields.put(REPOSITORY, repository);
        fields.put(TAG, tag);
        fields.put(ID, id);
        fields.put(REGISTRY, registry);
    }

    public String getRepository() {
        return fields.get(REPOSITORY);
    }

    public String getTag() {
        return fields.get(TAG);
    }

    public String getImageId() {
        return fields.get(ID);
    }

    public String getRegistry() {
        return fields.get(REGISTRY);
    }

    @Override
    public Map<String, String> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    @Override
    public String toJson() {
        final JsonStringMap jsonMap = DtoFactory.getInstance().createDto(JsonStringMap.class);
        jsonMap.putAll(fields);
        return jsonMap.toJson();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DockerInstanceSnapshotKey that = (DockerInstanceSnapshotKey)o;

        if (fields != null ? !fields.equals(that.fields) : that.fields != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fields != null ? fields.hashCode() : 0;
    }
}
