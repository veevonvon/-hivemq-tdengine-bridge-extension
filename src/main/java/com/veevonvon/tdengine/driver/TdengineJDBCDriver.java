package com.veevonvon.tdengine.driver;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.veevonvon.tdengine.driver.TdengineDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class TdengineJDBCDriver implements TdengineDriver {
    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(TdengineJDBCDriver.class);
    private Properties dbProperties = null;
    private DataSource dataSource = null;

    public TdengineJDBCDriver(Properties appProperties){
        if(dataSource != null) return;
        try {
            dbProperties = new Properties();
            Iterator it=appProperties.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry entry=(Map.Entry)it.next();
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                if(key.matches("^druid\\..*")){
                    dbProperties.setProperty(key.substring(6),value);
                }
                if(key.matches("^db\\..*")){
                    dbProperties.setProperty(key.substring(3),value);
                }
            }
            Class.forName(dbProperties.getProperty("driverClassName"));
            dataSource = DruidDataSourceFactory.createDataSource(dbProperties);
        }catch (Throwable e){
            LOGGER.error(e.getMessage());
        }
    }
    public int executeSql(String sql){
        boolean res = false;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()){
            res = stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            LOGGER.warn(String.format("Failed to execute SQL: %s\n", sql.toString()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.warn(String.format("Failed to execute SQL: %s\n", sql.toString()));
        }
        return res?1:0;
    }
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}