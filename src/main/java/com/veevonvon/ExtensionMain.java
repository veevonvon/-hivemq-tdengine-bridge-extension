
/*
 * Copyright 2018-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.veevonvon;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.auth.SimpleAuthenticator;
import com.hivemq.extension.sdk.api.events.EventRegistry;
import com.hivemq.extension.sdk.api.parameter.*;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.auth.SecurityRegistry;
import com.hivemq.extension.sdk.api.services.intializer.InitializerRegistry;
import com.veevonvon.tdengine.Tdengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 初始化插件时，读取参数设置，并创建超级表
 *
 * @author veevonvon
 */
public class ExtensionMain implements com.hivemq.extension.sdk.api.ExtensionMain {

    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(ExtensionMain.class);

    @Override
    public void extensionStart(final @NotNull ExtensionStartInput extensionStartInput, final @NotNull ExtensionStartOutput extensionStartOutput) {
        final File extensionHomeFolder = extensionStartInput.getExtensionInformation().getExtensionHomeFolder();
        try {
            Tdengine.init(extensionHomeFolder.getPath());
            Tdengine.createMetricsTable();
            // RunWithHiveMQ时调试使用
            //addAuth();
            addClientLifecycleEventListener();
            addPublishModifier();
            final ExtensionInformation extensionInformation = extensionStartInput.getExtensionInformation();
            LOGGER.info("Started " + extensionInformation.getName() + ":" + extensionInformation.getVersion());
        } catch (Exception e) {
            LOGGER.error("Exception thrown at extension start: ", e);
        }
    }
    @Override
    public void extensionStop(final @NotNull ExtensionStopInput extensionStopInput, final @NotNull ExtensionStopOutput extensionStopOutput) {
        Tdengine.close();
        final ExtensionInformation extensionInformation = extensionStopInput.getExtensionInformation();
        LOGGER.info("Stopped " + extensionInformation.getName() + ":" + extensionInformation.getVersion());
    }
    private  void addAuth(){
        final SecurityRegistry securityRegistry  = Services.securityRegistry();
        final SimpleAuthenticator simpleAuthenticator = new AllowAllAuth();
        securityRegistry.setAuthenticatorProvider((authenticatorProviderInput -> simpleAuthenticator));
    }
    private void addClientLifecycleEventListener() {
        final EventRegistry eventRegistry = Services.eventRegistry();
        final ClientListener clientListener = new ClientListener();
        eventRegistry.setClientLifecycleEventListener(input -> clientListener);
    }
    private void addPublishModifier() {
        final InitializerRegistry initializerRegistry = Services.initializerRegistry();
        final MessageInterceptor messageInterceptor = new MessageInterceptor();
        initializerRegistry.setClientInitializer((initializerInput, clientContext) -> clientContext.addPublishInboundInterceptor(messageInterceptor));
    }
}
