package com.github.farrellw

import java.time.Duration
import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory

object ScalaConsumer extends App {
  val bootstrapServer = "http://localhost:9092"
  val topic = "orders"

  val logger = LoggerFactory.getLogger(classOf[Consumer].getName)

  val properties = new Properties
  properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
  properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "my-first-application")

  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](properties)

  // subscribe consumer
  consumer.subscribe(util.Arrays.asList(topic))

  // poll for new data
  while ( {
    true
  }) {
    val duration: Duration = Duration.ofMillis(100)
    val records: ConsumerRecords[String, String] = consumer.poll(duration)
    import scala.collection.JavaConversions._
    for (record <- records) {
      logger.info("Key: " + record.key + " ,Value: " + record.value)
      logger.info("Partition: " + record.partition + " ,Offset " + record.offset)
    }
  }

}