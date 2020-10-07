
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

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.testcontainer.core.MavenHiveMQExtensionSupplier;
import com.hivemq.testcontainer.junit5.HiveMQTestContainerExtension;
import com.veevonvon.tdengine.MqttData;
import com.veevonvon.tdengine.Tdengine;
import com.veevonvon.tdengine.driver.TdengineRestfulDriver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试发送信息。启动测试前，先运行mvn package（勾选RunWithHiveMQ）
 *
 * @author veevonvon
 */
class MessageInterceptorIT {
    private static String HOST = "127.0.0.1";
    private static Integer PORT = 1883;

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void testPublishMsg() {
        final Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier("testPublish")
                .serverHost(HOST)
                .serverPort(PORT)
                .buildBlocking();
        client.connect();

        final Mqtt5BlockingClient.Mqtt5Publishes publishes = client.publishes(MqttGlobalPublishFilter.ALL);
        client.subscribeWith().topicFilter("testTopic").send();
        String msg = "message for test" + new Date().getTime();
        client.publishWith().topic("testTopic").payload(msg.getBytes(StandardCharsets.UTF_8)).send();

        Mqtt5Publish receive = null;
        try {
            receive = publishes.receive();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(receive.getPayload().isPresent());
        assertEquals(msg, new String(receive.getPayloadAsBytes(), StandardCharsets.UTF_8));
    }

}