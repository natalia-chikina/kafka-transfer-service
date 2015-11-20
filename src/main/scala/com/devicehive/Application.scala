package com.devicehive

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
      zooKeeperHost = args(0),
      brokerList = args(1),
      topic = args(2),
      groupId = args(3),
      decoder = new StringDecoder()
    ))

  def process(message: String) = {
    message.reverse
  }

    Source(publisher).map(m => process(m.message())).to(Sink(EventHubSubscriber(args(4)))).run()

}
