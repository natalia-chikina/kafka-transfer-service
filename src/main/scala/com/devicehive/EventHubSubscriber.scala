package com.devicehive

import java.util.Hashtable
import javax.jms.{Session, Destination, ConnectionFactory}
import javax.naming.{InitialContext, Context}

import org.reactivestreams.{Subscription, Subscriber}

class EventHubSubscriber(propertyFileName: String) extends Subscriber[String]{

  val env = new Hashtable[String, String]()
  env.put(Context.INITIAL_CONTEXT_FACTORY,
    "org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory")
  env.put(Context.PROVIDER_URL, propertyFileName)
  
  val context = new InitialContext(env)

  val connectionFactory = context.lookup("SBCF").asInstanceOf[ConnectionFactory]

  val queue = context.lookup("EventHub").asInstanceOf[Destination]

  val connection = connectionFactory.createConnection()

  val sendSession = connection.createSession(false,
    Session.AUTO_ACKNOWLEDGE)
  val sender = sendSession.createProducer(queue)


  override def onError(throwable: Throwable): Unit = {
    System.err.println(throwable.getMessage)
  }

  override def onSubscribe(subscription: Subscription): Unit = {
    subscription.request(Int.MaxValue)
  }

  override def onComplete(): Unit = {
  }

  override def onNext(t: String): Unit = {
    val message = sendSession.createBytesMessage()
    message.writeBytes(t.getBytes("UTF-8"))
    sender.send(message)
    System.out.println("Sent message")
  }

}

object EventHubSubscriber {
  def apply(uri: String) = new EventHubSubscriber(uri)
}
