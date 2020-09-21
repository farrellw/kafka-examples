# Java, Python, Scala, and Command Line Examples for Kafka Consumers and Producers

## Running Zookeeper and Kafka

### Starting Zookeeper
```
zookeeper-server-start config/zookeeper.properties
```

### Starting a Kafka Broker
Following three commands will start 3 kafka brokers running off 3 different config files.
```
kafka-server-start config/server.properties
kafka-server-start config/server1.properties
kafka-server-start config/server2.properties
```
Source: https://www.michael-noll.com/blog/2013/03/13/running-a-multi-broker-apache-kafka-cluster-on-a-single-node/

## CLI
Found in CLI/COMMANDS.md

## Python Consumer and Producer
Found in python directory

## Java Consumer and Producer
Found in src/main/java

## Scala Consumer and Producer
Found in src/main/java

## Faker
This repository uses faker to generate data to publish to a Kafka topic. Documentation on faker can be found http://dius.github.io/java-faker/apidocs/index.html and https://github.com/DiUS/java-faker
