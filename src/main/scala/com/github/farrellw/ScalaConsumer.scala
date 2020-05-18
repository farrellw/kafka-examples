package com.github.farrellw

import java.beans.BeanProperty
import java.time.Duration
import java.util
import java.util.Properties

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory

object ScalaConsumer extends App {
  val bootstrapServer = "35.225.13.175:9092"
  val topic: String = "orders"

  val logger = LoggerFactory.getLogger(classOf[Consumer].getName)

  val properties = new Properties
  properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
  properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "my-first-application")

  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](properties)

  // subscribe consumer
  consumer.subscribe(util.Arrays.asList(topic))

  val objectMapper = new ObjectMapper
  // poll for new data
  while ( {
    true
  }) {
    val duration: Duration = Duration.ofMillis(100)
    val records: ConsumerRecords[String, String] = consumer.poll(duration)

    records.forEach(record => {
      val order  = objectMapper.readValue(record.value(), classOf[Order])
      logger.info("Key: " + record.key + " ,Value: " + record.value)
      logger.info("Partition: " + record.partition + " ,Offset " + record.offset)
    })
  }
}