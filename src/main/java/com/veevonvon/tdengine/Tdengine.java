package com.veevonvon.tdengine;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.veevonvon.tdengine.driver.TdengineDriver;
import com.veevonvon.tdengine.driver.TdengineJDBCDriver;
import com.veevonvon.tdengine.driver.TdengineRestfulDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tdengine {
    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(Tdengine.class);
    public static Properties appProperties = null;
    private static String createMetricsSql = "";
    private static String createTableSql = "";
    private static String InsertSql = "";
    public static TdengineDriver driver = null;

    public static void init(String appPath){
        try{
            appProperties = new Properties();
            String propPath = appPath + File.separator + "application.properties";
            FileInputStream is = new FileInputStream(propPath);
            appProperties.load(is);
            is.close();
            switch (appProperties.getProperty("app.driver")){
                case "jdbc":
                    driver = new TdengineJDBCDriver(appProperties);
                    break;
                case "restful":
                    driver = new TdengineRestfulDriver(appProperties);
                    break;
                default:
                    driver = new TdengineRestfulDriver(appProperties);
                    break;
            }
            createMetricsSql = appProperties.getProperty("sql.createMetricsSql");
            createTableSql = appProperties.getProperty("sql.createTableSql").replace("{simplifyClientId}","{0}");
            InsertSql = appProperties.getProperty("sql.InsertSql")
                    .replace("{simplifyClientId}","{0}")
                    .replace("{timestamp}","{1}")
                    .replace("{topic}","{2}")
                    .replace("{payload}","{3}")
                    .replace("{qos}","{4}")
                    .replace("{clientId}","{5}");
        }catch (Throwable e){
            LOGGER.error(e.getMessage());
        }
    }

    public static int createMetricsTable(){
        return driver.executeSql(createMetricsSql);
    }
    public static int createTable(String clientId) {
        String tableSql = MessageFormat.format(createTableSql, fixTableName(clientId));
        return driver.executeSql(tableSql);
    }
    public static MqttData buildMqttData(String clientId, long time, String topic, ByteBuffer payload, int qos) {
        String charsetStr = appProperties.getProperty("app.charset");
        Charset charset = Charset.forName(charsetStr);
        String payloadStr = charset.decode(payload).toString();
        return new MqttData(clientId, time, topic, payloadStr, qos);
    }
    public static int insert(MqttData data) {
        String payload = filtString(data.getPayload());
        if(appProperties.getProperty("app.setPayloadBase64").equals("true")){
            payload = data.getPayloadBase64();
        }
        String sql = MessageFormat.format(InsertSql,
                fixTableName(data.getClientId()),
                String.valueOf(data.getTimestamp()),
                filtString(data.getTopic()),
                payload,
                data.getQos(),
                filtString(data.getClientId())
        );
        return driver.executeSql(sql);
    }
    private static String filtString(String str){
        return str.replace("'","''").replace("\\","\\\\");
    }
    private static String fixTableName(String name){
        String regEx="[a-zA-Z0-9_]";
        Pattern p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(name);
        StringBuffer newName = new StringBuffer();
        while(m.find()){
            newName.append(m.group());
        }
        return newName.toString();
    }
}
