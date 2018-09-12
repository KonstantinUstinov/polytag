package com.polygon.tags

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import com.polygon.tags.utils.ConfigProvider

object Boot extends App with ConfigProvider {
  implicit val system: ActorSystem = ActorSystem("polytag-service")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val logger = Logging(system, getClass)

  val server = new RoutingServer()
  server.startServer(config.getString("http.interface"), config.getInt("http.port"))
}
