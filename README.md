# HiveMQ Extension

## 简介
- 这是一个HiveMQ插件，将publish消息插入TDengine，支持jdbc（使用druid作为连接池）和restful方式

## 快速开始
#### 部署TDengine
- 参考官方文档[https://www.taosdata.com/cn/getting-started/](https://www.taosdata.com/cn/getting-started/)
- 手动执行创建数据库sql语句，例如```CREATE DATABASE td;```
#### 部署HiveMQ
- 参考官方文档[https://www.hivemq.com/docs/hivemq/4.4/user-guide/introduction.html](https://www.hivemq.com/docs/hivemq/4.4/user-guide/introduction.html)
#### 插件部署
- 将插件的压缩包`tdengine-1.0-SNAPSHOT-distribution.zip`解压至`HiveMQ`的`extensions`目录
- 修改`application.properties`的数据库连接信息，sql语句等
- HiveMQ插件部署方法参考
#### 测试客户端连接
- 例如使用客户端工具`MQTT X`连接`HiveMQ`，参考官方使用手册[https://mqttx.app/cn/docs/](https://mqttx.app/cn/docs/)
- 发送消息后检查tdengine数据库数据，插件会新建超级表，根据每一个clientId创建表，publish消息会插入对应的表

## 编译方法
- ```mvn clean package```

## 代码架构
- java版本:11,java sdk实现：adopt-openj9-11
- `ExtensionMain.java` 为插件主入口，负责读取插件设置，初始化数据库连接及创建超级表，设置客户端连接和mqtt消息的拦截器
- `ClientListener.java` 侦听mqtt客户端的连接，为连接成功的客户端新建表
- `MessageInterceptor.java` 侦听publish消息，异步将数据插入数据库，异步过程使用```CompletableFuture.runAsync()```
- `AllowAllAuth.java` 仅供开发调试使用。RunWithHiveMQ模式下调试代码
- `tdengine/*` 连接tdengine数据库的具体实现，jdbc连接方式搭配druid连接池，restful连接方式使用okhttp作为客户端
- `application.properties` 所有插件设置，详情见文件注释。这里可配置执行的sql语句

## 注意事项
- `application.properties`中的sql语句包含数据库前缀。restful连接方式必须包含数据库前缀。参考资料：[https://www.taosdata.com/cn/documentation/connector/#RESTful-Connector](https://www.taosdata.com/cn/documentation/connector/#RESTful-Connector)
- jdbc的连接url包含连接数据库名称，需与sql语句设置的数据库前缀保持一致，sql语句也可不包含数据库前缀
- 表名默认按照每个client单独一个表，如果不需要按照每个client单独一个表，sql语句的表名写死即可
- 由于表名不能以数字开头，所以clientId不能以数字开头，建议以大小写字母开头，clientid设置表名时，会过滤特殊字符
- 使用`mqttloader`测试时，无法识别消息正文，需要设置`app.setPayloadBase64=true`

## License
- Apache License Version 2.0

## 性能测试
- Tdengine V2.0.4.0  配置：vm虚拟机，centos 7.8，单核4G   
- HiveMQ V4.4.1  配置：裸机win10，I7 8700，32.0 GB

#### jdbc
```
.\mqttloader -b tcp://127.0.0.1:1883 -p 10 -s 1 -m 100

-----Publisher-----
Maximum throughput[msg/s]: 1000
Average throughput[msg/s]: 1000.00
Number of published messages: 1000
Per second throughput[msg/s]: 1000

-----Subscriber-----
Maximum throughput[msg/s]: 1000
Average throughput[msg/s]: 1000.00
Number of received messages: 1000
Per second throughput[msg/s]: 1000
Maximum latency[ms]: 58
Average latency[ms]: 30.18
```

```
.\mqttloader -b tcp://127.0.0.1:1883 -p 100 -s 1 -m 100

-----Publisher-----
Maximum throughput[msg/s]: 10000
Average throughput[msg/s]: 10000.00
Number of published messages: 10000
Per second throughput[msg/s]: 10000

-----Subscriber-----
Maximum throughput[msg/s]: 6508
Average throughput[msg/s]: 5000.00
Number of received messages: 10000
Per second throughput[msg/s]: 3492, 6508
Maximum latency[ms]: 781
Average latency[ms]: 377.03
```

#### restful
```
.\mqttloader -b tcp://127.0.0.1:1883 -p 10 -s 1 -m 100
-----Publisher-----
Maximum throughput[msg/s]: 1000
Average throughput[msg/s]: 1000.00
Number of published messages: 1000
Per second throughput[msg/s]: 1000

-----Subscriber-----
Maximum throughput[msg/s]: 661
Average throughput[msg/s]: 500.00
Number of received messages: 1000
Per second throughput[msg/s]: 661, 339
Maximum latency[ms]: 1122
Average latency[ms]: 822.15
```

```
.\mqttloader -b tcp://127.0.0.1:1883 -p 100 -s 1 -m 100
-----Publisher-----
Maximum throughput[msg/s]: 6220
Average throughput[msg/s]: 5000.00
Number of published messages: 10000
Per second throughput[msg/s]: 3780, 6220

-----Subscriber-----
Maximum throughput[msg/s]: 5580
Average throughput[msg/s]: 2500.00
Number of received messages: 10000
Per second throughput[msg/s]: 250, 550, 5580, 3620
Maximum latency[ms]: 2742
Average latency[ms]: 1702.85
```