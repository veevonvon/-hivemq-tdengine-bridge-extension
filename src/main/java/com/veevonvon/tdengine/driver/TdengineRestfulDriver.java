package com.veevonvon.tdengine.driver;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.veevonvon.ExtensionMain;
import com.veevonvon.tdengine.driver.TdengineDriver;
import okhttp3.*;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdengineRestfulDriver implements TdengineDriver {
    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(TdengineRestfulDriver.class);
    public Properties properties = null;
    public String url = "";
    public String authorization = "";
    public TdengineRestfulDriver(Properties appProperties){
        properties = appProperties;
        url = properties.getProperty("restful.url");
        authorization = properties.getProperty("db.username")+":"+properties.getProperty("db.password");
        try {
            authorization = Base64.getEncoder().encodeToString(authorization.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public int executeSql(String sql){
        int res = 0;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody body = null;

        body = RequestBody.create(sql, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request
                .Builder()
                .post(body)
                .url(url)
                .addHeader("Authorization","Basic "+authorization)
                .addHeader("Content-type", "text/html;charset=utf-8")
                .build();
        try (Response response = mOkHttpClient.newCall(request).execute()) {
            String resp = response.body().string();
            JSONObject jo = JSONObject.parseObject(resp);
            String status = jo.getString("status");
            if(status.equals("succ")){
                res = 1;
            }else{
                throw new Exception(resp);
            }
        }catch (Exception e){
            LOGGER.error( e.getMessage());
            LOGGER.warn(String.format("Failed to execute SQL: %s\n", sql.toString()));
            return 0;
        }
        return res;
    }
    public void close(){}
}
