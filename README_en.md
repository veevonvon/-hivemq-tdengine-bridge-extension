# HiveMQ Extension

## Introduction
- This is a HiveMQ Extension that inserts publis messages into TDengine , supportint jdbc (using drid as a connection pool) and restful.

## Quick start
#### Deploy TDengine
- Refer to the official documentation.[https://www.taosdata.com/cn/getting-started/](https://www.taosdata.com/cn/getting-started/)
- Manually execute the create database sql statement, for example:```CREATE DATABASE td;```
#### Deploy HiveMQ
- Refer to the official documentation.[https://www.hivemq.com/docs/hivemq/4.4/user-guide/introduction.html](https://www.hivemq.com/docs/hivemq/4.4/user-guide/introduction.html)
#### Deploy Extension
- Unzip the extension compression pack `hivemq-tdengine-bridge-extension-1.0.0-distribution.zip` to the `extensions`directory of `HiveMQ`
- Modify `application.properties` database connection information, sql statements, etc.
- HiveMQ deployment method reference refer to [https://www.hivemq.com/docs/hivemq/4.4/extensions/deployment.html](https://www.hivemq.com/docs/hivemq/4.4/extensions/deployment.html)
#### Test the client connection.
- For example, use the client tool `MQTT X` to connect `HiveMQ`,Refer to the official documentation.[https://mqttx.app/cn/docs/](https://mqttx.app/cn/docs/)
- After sending the message, check the tdengine database data, the extension creates a new super table, creates the table based on each clientId, and the publish message inserts the corresponding table.

## Compilation Extension
- ```mvn clean package```

## Code schema
- java version :11,java sdk version：adopt-openj9-11
- `ExtensionMain.java` is the main portal for extension and is responsible for reading extension settings, initializing database connections and creating super tables, setting up interceptors for client connections and mqtt messages.
- `ClientListener.java` listens to the connection of the mqtt client and creates a new table for the successful client.
- `MessageInterceptor.java` listens for publish messages, asynchronously inserts data into the database, and asynchronous processes use ```CompleteAbleFuture.runAsync()```
- `AllowAllAuth.java` is for development and debugging purposes only. Debug code in RunWithHiveMQ mode.
- `tdengine/*` is the specific implementation of the connection tdengine database, jdbc connection with drid connection pool, restful connection method using okhttp as the client.
- `application.properties` all extension settings, see file notes for details. The sql statement executed can be configured here.

## Precautions
- The sql statement in `application.properties` contains the database prefix. The restful connection must contain a database prefix.Refer to the official documentation. [https://www.taosdata.com/cn/documentation/connector/#RESTful-Connector](https://www.taosdata.com/cn/documentation/connector/#RESTful-Connector)
- Connection's url of jdbc contains the connection database name, which needs to be consistent with the database prefix set by the sql statement, or the sql statement does not contain the database prefix.
- The table name defaults to a separate table per, and if you don't need to follow a single table per, the table name of the sql statement sets a fixed table name.
- Because the table name cannot begin with a number, clitId cannot begin with a number, and it is recommended that special characters be filtered when the clientid sets the table name.
- When using the `mqttloader` test, the message body is not recognized and `app.setPayloadBase64=true` needs to be set.

## License
- Apache License Version 2.0

## Performance testing.
- Tdengine V2.0.4.0  Configuration:vmbox Machine, centos 7.8, single core cpu and 4G memory.
- HiveMQ V4.4.1 Configuration: win10, cpu I7 8700, 32.0 GB memory.

#### jdbc mode
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

#### restful mode
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