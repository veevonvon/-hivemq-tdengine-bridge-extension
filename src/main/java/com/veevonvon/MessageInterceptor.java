
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
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.veevonvon.tdengine.MqttData;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import com.veevonvon.tdengine.Tdengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mqtt消息拦截，异步插入publish消息
 *
 * @author veevonvon
 */
public class MessageInterceptor implements PublishInboundInterceptor {

    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(MessageInterceptor.class);
    @Override
    public void onInboundPublish(final @NotNull PublishInboundInput publishInboundInput, final @NotNull PublishInboundOutput publishInboundOutput) {
        String clientId = publishInboundInput.getClientInformation().getClientId();
        @NotNull PublishPacket packet = publishInboundInput.getPublishPacket();
        int qos = packet.getQos().getQosNumber();
        String topic = packet.getTopic();
        ByteBuffer inPayload = packet.getPayload().get();
        long time = packet.getTimestamp();
        MqttData data =Tdengine.buildMqttData(clientId, time, topic, inPayload, qos);
        CompletableFuture.runAsync(()->{
            try {
                Tdengine.insert(data);
            } catch (Throwable e) {
                LOGGER.error(e.getMessage());
            }
        });
    }

}