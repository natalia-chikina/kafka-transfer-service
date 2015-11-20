package com.devicehive

import java.util.Hashtable
import javax.jms.{Session, Destination, ConnectionFactory}
import javax.naming.{InitialContext, Context}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.softwaremill.react.kafka.KafkaMessages.StringKafkaMessage
import kafka.serializer.{StringDecoder, StringEncoder}
import org.reactivestreams.{Subscription, Publisher, Subscriber}
import com.softwaremill.react.kafka.{ReactiveKafka, ProducerProperties, ConsumerProperties}

object Application extends App  {
    implicit val actorSystem = ActorSystem("ReactiveKafka")
    implicit val materializer = ActorMaterializer()

    val kafka = new ReactiveKafka()
    val publisher: Publisher[StringKafkaMessage] = kafka.consume(ConsumerProperties(
      brokerList = args(1),
      zooKeeperHost = args(0),
      topic = "test",
      groupId = "groupName",
      decoder = new StringDecoder()
    ))

    Source(publisher).map(_.message()).to(Sink(EventHubSubscriber(args(2)))).run()

}
