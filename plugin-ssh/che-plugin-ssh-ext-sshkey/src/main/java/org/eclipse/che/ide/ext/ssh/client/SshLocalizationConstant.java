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
package org.eclipse.che.ide.ext.ssh.client;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public interface SshLocalizationConstant extends Messages {
    @Key("cancelButton")
    String cancelButton();

    @Key("closeButton")
    String closeButton();

    @Key("uploadButton")
    String uploadButton();

    @Key("browseButton")
    String browseButton();

    @Key("hostFieldTitle")
    String hostFieldTitle();

    @Key("fileNameFieldTitle")
    String fileNameFieldTitle();

    @Key("generate.sshKey.title")
    String generateSshKeyTitle();

    @Key("generate.sshKey.hostname")
    String generateSshKeyHostname();

    @Key("uploadSshKeyViewTitle")
    String uploadSshKeyViewTitle();

    @Key("host.validation.error")
    String hostValidationError();

    @Key("key.manager.uploadButton")
    String managerUploadButton();

    @Key("key.manager.generateButton")
    String managerGenerateButton();

    @Key("key.manager.title")
    String sshManagerTitle();

    @Key("key.manager.category")
    String sshManagerCategory();

    @Key("public.sshkey.field")
    String publicSshKeyField();

    @Key("delete.sshkey.question")
    SafeHtml deleteSshKeyQuestion(String host);

    @Key("delete.sshkey.title")
    String deleteSshKeyTitle();

    @Key("delete.sshkey.failed")
    String deleteSshKeyFailed();

    @Key("sshkeys.provider.not.found")
    String sshKeysProviderNotFound(String host);

    @Key("loader.deleteSshKey.message")
    String loaderDeleteSshKeyMessage(String host);

    @Key("loader.getSshKeys.message")
    String loaderGetSshKeysMessage();

    @Key("loader.getPublicSshKey.message")
    String loaderGetPublicSshKeyMessage(String host);
}