package com.veevonvon.tdengine;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Optional;

public class MqttData {

    long timestamp;
    int qos;
    String clientId;
    String topic;
    String payload;

    public MqttData(String clientId, long timestamp, String topic, String payload, int qos) {
        this.timestamp = timestamp;
        this.qos = qos;
        this.clientId = clientId;
        this.topic = topic;
        this.payload = payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getQos() {
        return qos;
    }

    public String getClientId() {
        return clientId;
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }
    public String getPayloadBase64() {
        try {
            return Base64.getEncoder().encodeToString(payload.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return payload;
    }
}
