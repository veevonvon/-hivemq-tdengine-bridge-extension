package com.veevonvon;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.veevonvon.tdengine.MqttData;
import com.veevonvon.tdengine.Tdengine;
import com.veevonvon.tdengine.driver.TdengineRestfulDriver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * tdengine测试，新建库，新建超级表，新建client表，插入数据
 *
 * @author veevonvon
 */
public class TdengineIT {
    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void testCreateDb() {
        Tdengine.init("src/main/resources");
        if(!Tdengine.appProperties.getProperty("app.driver").equals("restful")){
            Tdengine.appProperties.setProperty("app.driver","restful");
            Tdengine.driver = new TdengineRestfulDriver(Tdengine.appProperties);
        }
        int res = Tdengine.driver.executeSql("CREATE DATABASE  IF NOT EXISTS td");

        assertEquals(1, res);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void testCreateTable() {
        Tdengine.init("src/main/resources");
        int res = Tdengine.createMetricsTable();
        assertEquals(1,res);
        res = Tdengine.createTable("testclient");
        assertEquals(1,res);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void testInsert() {
        String clientId = "testclient";
        int qos = 0;
        String topic = "topic123";
        long time = new Date().getTime();
        ByteBuffer inPayload = ByteBuffer.wrap("message for test".getBytes(StandardCharsets.UTF_8));

        Tdengine.init("src/main/resources");
        Tdengine.createMetricsTable();
        Tdengine.createTable(clientId);
        MqttData data =Tdengine.buildMqttData(clientId, time, topic, inPayload, qos);
        int res = Tdengine.insert(data);
        assertEquals(1,res);
    }
}
