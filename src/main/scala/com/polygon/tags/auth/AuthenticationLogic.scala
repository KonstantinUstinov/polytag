package com.polygon.tags.auth

import java.net.URLDecoder
import java.util.Date
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import com.polygon.tags.dao.{AuthUser, AuthUserDAO, UserTokenDAO}
import com.polygon.tags.utils.ConfigProvider
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONObjectID

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}
import scalaoauth2.provider._


object AuthenticationLogic {
  val MaxPasswordLength = 256

  def extractUser(ctx: AuthorizationRequest): Try[(String, String)] = Try {
    ctx.parseClientCredential match {
      case Some(either) =>
        either match {
          case Right(creds) =>
            if (creds.clientId.length > MaxPasswordLength || creds.clientSecret.getOrElse("").length > MaxPasswordLength) {
              throw new InvalidGrant("username or password is incorrect")
            } else (creds.clientId, creds.clientSecret.getOrElse(""))
          case Left(e) =>
            throw e
        }

      case _ => throw new InvalidGrant("username or password is incorrect")
    }
  }

  def generateCode: String = BSONObjectID.generate().stringify

}

trait AuthenticationLogic extends AuthUserDAO with UserTokenDAO with ConfigProvider {

  implicit val executor: ExecutionContextExecutor
  import AuthenticationLogic._

  lazy val tokenExpireIntervalSeconds: Long = config.getDuration("auth.server.token-expire-interval", TimeUnit.SECONDS)

  val Authorization = "Authorization"

  def authenticateUser(request: AuthorizationRequest, state: Option[String],
                       redirectUri: String, clientCreds: ClientCredential): Future[(Int, Either[OAuthError, CodeResponse])] = {

    extractUser(request) match {

      case Failure(ex: OAuthError) =>
        Future.successful(ex.statusCode -> Left(ex))

      case Success((name, pass)) =>
        doUserAuth(name, pass, state, redirectUri, clientCreds) map {
          case Right(result) =>
            StatusCodes.OK.intValue -> Right(result)

          case Left(error) =>
            error.statusCode -> Left(error)
        }
    }
  }

  private def doUserAuth(name: String, pass: String,
                         state: Option[String], redirectUri: String,
                         clientCreds: ClientCredential): Future[Either[OAuthError, CodeResponse]] = {

    user_dao.getUserByName(name, pass).flatMap {
      case None => Future.successful(Left(new InvalidClient("username or password is incorrect")))
      case Some(user) =>
        val code = generateCode
        saveCode(code, AuthInfo(user, Some(clientCreds.clientId), None, Some(redirectUri + s"/${user.name}"))) map {
          _ => Right(CodeResponse(code, state))
        }

    }
  }

  def saveCode(code: String, ai: AuthInfo[AuthUser]): Future[WriteResult] = {
    tokenDao.saveTokenAndAuthInfo(AccessToken(code, None, None, Some(tokenExpireIntervalSeconds), new Date()), ai)
  }

  def generateTokenByCode(code: String, userName: String): Future[Option[AccessToken]] = {
    tokenDao.findByToken(code) map {
      case Some(authInfo) if authInfo.user.name == userName => Some(authInfo.asToken)
      case _ => None
    }
  }

  def extractAuthToken(req: HttpRequest): Option[String] = {
    req.cookies.find(_.name == Authorization) match {
      case Some(cookie) => Some(URLDecoder.decode(cookie.value, "UTF-8"))
      case None =>
        req.headers.find(_.name == Authorization) match {
          case Some(header) => Some(header.value)
          case None => None
        }
    }
  } map extractToken

  def extractToken(header: String): String = {
    val matcher = AuthHeader.REGEXP_AUTHORIZATION.findFirstMatchIn(header).getOrElse {
      throw new InvalidRequest("Authorization header is invalid")
    }
    matcher.group(2)
  }

}
