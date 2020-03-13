package com.github.farrellw;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CovidTweets {
    Logger logger = LoggerFactory.getLogger(CovidTweets.class.getName());

    public CovidTweets(){}

    public static void main(String[] args) throws Exception {
        new CovidTweets().run();
    }

    public void run() throws Exception{
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);

        // Create a twitter client
        Client client = createTwitterClient(msgQueue);

        client.connect();
        // Create a kafka producer
        KafkaProducer<String, String> producer = createKafkaProducer();

        // on a different thread, or multiple different threads....
        int i = 1000;
        while (!client.isDone() && i >= 0) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch ( InterruptedException e ){
                e.printStackTrace();
                client.stop();
            }
            i -= 1;
            if(msg != null){
                ProducerRecord<String, String> record = new ProducerRecord<>("covid-tweets", msg);
                producer.send(record);
                logger.info(msg);
            }
        }
        logger.info("End of application");
    }

    public Client createTwitterClient(BlockingQueue<String> msgQueue) throws Exception {
        Dotenv dotenv = Dotenv.load();
        String consumerKey = dotenv.get("CONSUMER_KEY");
        String consumerSecret = dotenv.get("CONSUMER_SECRET");
        String token = dotenv.get("TOKEN");
        String tokenSecret = dotenv.get("TOKEN_SECRET");
        if(consumerKey == null || consumerSecret == null || token == null || tokenSecret == null){
            throw new Exception("Required environment variable not set.");
        }

        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

        List<String> terms = Lists.newArrayList("corona");
        hosebirdEndpoint.trackTerms(terms);

        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        return builder.build();
    }

    public KafkaProducer<String, String> createKafkaProducer(){
        String gcpBootstrapServer = "35.208.65.122:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, gcpBootstrapServer);
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "1");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        return new KafkaProducer<String, String>(properties);
    }
}