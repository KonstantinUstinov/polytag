package com.polygon.tags.dao

import com.polygon.tags.utils.ConfigProvider
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONLong, BSONObjectID, BSONString}

import scala.concurrent.Future
import scalaoauth2.provider.{AccessToken, AuthInfo}

object UserTokenDAO extends ConfigProvider {

  import MongoDriverContext.mongoExecutionContext
  implicit lazy val tokenContext = MongoDriverContext[TokenDto](config, "mongo.token")

  implicit object UserTokenReader extends BSONDocumentReader[TokenDto] {
    def read(bson: BSONDocument): TokenDto = {
      val opt = for {
        token <- bson.getAs[BSONObjectID]("_id")
        refreshToken <- bson.getAs[String]("refreshToken")
        scope <- bson.getAs[String]("scope")
        expiresIn <- bson.getAs[Long]("expiresIn")
        createdAt <- bson.getAs[BSONDateTime]("createdAt")
        clientId <- bson.getAs[String]("clientId")
        authInfoScope <- bson.getAs[String]("authInfoScope")
        redirectUri <- bson.getAs[String]("redirectUri")
        user <- bson.getAs[BSONDocument]("user")

      } yield TokenDto(token.stringify, Some(refreshToken), Some(scope), Some(expiresIn), createdAt, getUser(user), Some(clientId), Some(authInfoScope), Some(redirectUri))
      opt.get
    }

    def getUser(bson: BSONDocument): TokenUserDto = {
      TokenUserDto(bson.getAs[String]("name").get, bson.getAs[String]("ssoToken"))
    }

  }

  implicit object UserTokenWriter extends BSONDocumentWriter[TokenDto] {
    def write(tocken: TokenDto): BSONDocument = {
      BSONDocument(
        "_id" -> BSONObjectID.parse(tocken.token).get,
        "refreshToken" -> BSONString(tocken.refreshToken.getOrElse("")),
        "scope" -> BSONString(tocken.scope.getOrElse("")),
        "expiresIn" -> BSONLong(tocken.expiresIn.getOrElse(0)),
        "createdAt" -> tocken.createdAt,
        "clientId" -> tocken.clientId,
        "authInfoScope" -> BSONString(tocken.authInfoScope.getOrElse("")),
        "redirectUri" -> BSONString(tocken.redirectUri.getOrElse("")),
        "user" -> BSONDocument("name" -> tocken.user.name, "ssoToken" -> tocken.user.ssoToken.getOrElse(""))
      )
    }
  }

  class UserTokenDAO extends GenericDAO[TokenDto] {

    def removeByToken(token: String): Future[WriteResult] = remove(BSONObjectID.parse(token).get)

    def findByToken(token: String): Future[Option[TokenDto]] = headOption(BSONDocument("_id" -> BSONObjectID.parse(token).get))

    def saveTokenAndAuthInfo(token: AccessToken, authInfo: AuthInfo[AuthUser]): Future[WriteResult] = save(
      TokenDto(token.token, token.refreshToken, token.scope, token.expiresIn, BSONDateTime(token.createdAt.getTime),
        TokenUserDto(authInfo.user.name, None), authInfo.clientId, authInfo.scope, authInfo.redirectUri)
    )

  }

  lazy val tokenDao = new UserTokenDAO

}

trait UserTokenDAO {
  def tokenDao = UserTokenDAO.tokenDao
}
