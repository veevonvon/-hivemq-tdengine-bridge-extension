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
import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.packets.general.MqttVersion;
import com.veevonvon.tdengine.Tdengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端连接时根据clientid创建表，每一个clientid会有自己独立的表
 *
 * @author veevonvon
 */
public class ClientListener implements ClientLifecycleEventListener {
    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(ClientListener.class);

    @Override
    public void onMqttConnectionStart(final @NotNull ConnectionStartInput connectionStartInput) {
        final MqttVersion version = connectionStartInput.getConnectPacket().getMqttVersion();
        String clientId = connectionStartInput.getClientInformation().getClientId();
        try {
            Tdengine.createTable(clientId);
        }catch (Throwable e){
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onAuthenticationSuccessful(final @NotNull AuthenticationSuccessfulInput authenticationSuccessfulInput) {

    }

    @Override
    public void onDisconnect(final @NotNull DisconnectEventInput disconnectEventInput) {

    }
}
