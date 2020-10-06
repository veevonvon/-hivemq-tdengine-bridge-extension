package com.veevonvon;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.auth.parameter.PublishAuthorizerInput;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.veevonvon.tdengine.MqttData;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.alibaba.fastjson.JSONObject;
import com.veevonvon.tdengine.Tdengine;
import org.junit.jupiter.api.Test;

public class Index {
    public static void main(String[] argv){
//        String str = "……^1dsf  の  adS   DFASFSADF阿德斯防守对方asdfsadf37《？：？@%#￥%#￥%@#%#@%^><?1234";
//        String regEx="[a-zA-Z0-9_]";
//        Pattern p   =   Pattern.compile(regEx);
//        Matcher m   =   p.matcher(str);
//        StringBuffer sb = new StringBuffer();
//        while(m.find()){
//            sb.append(m.group());
//        }
//        System.out.println(sb);

        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");
        System.setProperty("https.proxyPort", "8888");

        PublishAuthorizerInput publishInboundInput;
        String clientId = "atest1";
        int qos = 0;
        String topic = "123";
        long time = new Date().getTime();
        ByteBuffer inPayload = ByteBuffer.wrap("你的钱".getBytes(StandardCharsets.UTF_8));

        Tdengine.init("src/main/resources");
        Tdengine.createMetricsTable();
        Tdengine.createTable(clientId);
        MqttData data =Tdengine.buildMqttData(clientId, time, topic, inPayload, qos);
        Tdengine.insert(data);

    }
}
