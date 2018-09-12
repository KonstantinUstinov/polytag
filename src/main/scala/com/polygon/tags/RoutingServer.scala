package com.polygon.tags

import java.security.{KeyStore, SecureRandom}
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.event.Logging.LogLevel
import akka.http.scaladsl.{ConnectionContext, Http}
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteResult.Complete
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.server.directives.{DebuggingDirectives, LogEntry, LoggingMagnet}
import com.polygon.tags.routes.Service
import javax.net.ssl.{KeyManagerFactory, SSLContext}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}


class RoutingServer(implicit val system: ActorSystem,
                    implicit val materializer: ActorMaterializer,
                    implicit val executor: ExecutionContextExecutor,
                    implicit val logger: LoggingAdapter) extends Service {
  def startServer(address: String, port: Int) = {
    val loggedRoute = requestMethodAndResponseStatusAsInfo(Logging.InfoLevel, routes)

    val https: ConnectionContext = {
      val password = "hof".toCharArray
      val context = SSLContext.getInstance("TLS")
      val ks = KeyStore.getInstance("PKCS12")
      ks.load(getClass.getClassLoader.getResourceAsStream("keys/keystore.pkcs12"), password)
      val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
      keyManagerFactory.init(ks, password)
      context.init(keyManagerFactory.getKeyManagers, null, new SecureRandom)
      // start up the web server
      ConnectionContext.https(context)
    }

    Http().bindAndHandle(loggedRoute, address, port, connectionContext = https)
  }

  def requestMethodAndResponseStatusAsInfo(level: LogLevel, route: Route)
                                          (implicit m: Materializer, ex: ExecutionContext) = {

    def akkaResponseTimeLoggingFunction(loggingAdapter: LoggingAdapter, requestTimestamp: Long)(req: HttpRequest)(res: Any): Unit = {
      val entry = res match {
        case Complete(resp) =>
          val responseTimestamp: Long = System.currentTimeMillis()
          val elapsedTime: Long = responseTimestamp - requestTimestamp
          val loggingString = "Logged Request:" + req.method + ":" + req.uri + ":" + resp.status + ":" + elapsedTime
          LogEntry(loggingString, level)
        case anythingElse =>
          LogEntry(s"$anythingElse", level)
      }
      entry.logTo(loggingAdapter)
    }
    DebuggingDirectives.logRequestResult(LoggingMagnet(log => {
      val requestTimestamp = System.currentTimeMillis()
      akkaResponseTimeLoggingFunction(log, requestTimestamp)
    }))(route)

  }
}
