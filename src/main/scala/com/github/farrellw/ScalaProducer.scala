package com.github.farrellw

import java.util.Properties

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import com.github.javafaker.Faker

object ScalaProducer extends App {
  val bootstrapServer = "35.225.13.175:9092"
  val topic = "orders"

  val logger = LoggerFactory.getLogger(classOf[Producer])

  // create Producer properties
  val properties = new Properties
  properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
  properties.setProperty(ProducerConfig.ACKS_CONFIG, "1")
  properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

  // create the producer
  val producer = new KafkaProducer[String, String](properties)

  val objectMapper = new ObjectMapper
  val faker = new Faker

  val recordsToCreate = 10
  val r = (1 to recordsToCreate).toList
  r.map(id => {
    val order = new Order(faker.commerce.price, faker.commerce.productName)
    val customerId = Integer.toString(id)
    val jsonString = objectMapper.writeValueAsString(order)
    println(jsonString)

   new ProducerRecord[String, String](topic, customerId, jsonString)
  }).foreach(record => {

    producer.send(record, new Callback() {
      override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {
        if (e == null) {
          logger.info("Received new metadata. \n" + "Topic:" + recordMetadata.topic() + "\n"
            + "Partition:" + recordMetadata.partition() + "\n" + "Offset: "
            + recordMetadata.offset() + "\n" + "Timestamp: " + recordMetadata.timestamp());
        }
        else logger.error("Error while producing", e)
      }
    })
  })
  producer.flush()


  producer.close()
}