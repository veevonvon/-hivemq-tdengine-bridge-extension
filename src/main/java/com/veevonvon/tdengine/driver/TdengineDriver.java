package com.veevonvon.tdengine.driver;

import java.util.Properties;

public interface TdengineDriver {
    public int executeSql(String sql);
    public void close();
}
