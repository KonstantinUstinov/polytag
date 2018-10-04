package com.polygon.tags.routes

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpRequest, StatusCodes, Uri}
import akka.http.scaladsl.model.headers._
import akka.stream.Materializer
import com.polygon.tags.utils.ConfigProvider
import scala.concurrent.ExecutionContextExecutor
import akka.http.scaladsl.server.Directives._
import com.polygon.tags.auth.AuthenticationLogic
import com.polygon.tags.utils.AkkaOAuthHelpers._
import scala.util.Success
import scalaoauth2.provider.ClientCredential

trait LoginService extends ConfigProvider with SprayJsonSupport with AuthenticationLogic {

  implicit val system: ActorSystem
  implicit val materializer: Materializer
  implicit val executor: ExecutionContextExecutor
  implicit val logger: LoggingAdapter

  import com.polygon.tags.auth.TokenJsonProtocol._

  def loginServiceRoute =
    pathPrefix("login") {
      post {
        loginRoute
      }
    } ~
    pathPrefix("authorized") {
      get {
        authorizedRoute
      }
    } ~
    pathPrefix("logout") {
      get {
        authLogoutRoute
      }
    }

  def loginRoute = parameters("state".?, "redirect_uri", "client_id", "client_secret".?, "response_type" ! "code") { (state, redirectUri, clientId, client_secret) =>
    respondWithHeaders(RawHeader("Content-Type", "application/json")) {
      respondWithHeader(`Cache-Control`(CacheDirectives.`no-store`)) {
        respondWithHeader(RawHeader("Pragma", "no-cache")) {
          extractRequestContext { ctx =>
            complete(authenticateUser(ctx, state, redirectUri, ClientCredential(clientId, client_secret)))
          }
        }
      }
    }
  }

  def authorizedRoute = (path(Segment) & parameter('code)) { (userId, code) =>
    onComplete(generateTokenByCode(code, userId)) {
      case Success(Some(token)) =>
        setCookie(authCookie(token.token).copy(secure = true), userCookie(userId).copy(secure = true)) {
          redirect(Uri("/index.html"), StatusCodes.Found)
        }
      case _ =>
        complete(StatusCodes.Unauthorized -> "invalid_auth_code")
    }
  }

  def authLogoutRoute =
    extract(_.request) { req =>
      val token = extractAuthToken(req).getOrElse("")
    }

  def authCookie(token: String) = HttpCookie(Authorization, s"Bearer%20$token", httpOnly = false, secure = true, path = Some("/"), expires = None)

  def userCookie(userName: String) = HttpCookie("user", userName, httpOnly = false, secure = true, path = Some("/"), expires = None)


}
