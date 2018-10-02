package com.polygon.tags.dao

import com.polygon.tags.utils.ConfigProvider
import reactivemongo.bson.{BSONArray, BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID, BSONString}

import scala.concurrent.Future

object AuthUserDAO extends ConfigProvider {

  import MongoDriverContext.mongoExecutionContext
  implicit lazy val userContext = MongoDriverContext[AuthUser](config, "mongo.user")

  implicit object UserReader extends BSONDocumentReader[AuthUser] {
    def read(bson: BSONDocument): AuthUser = {
      val opt = for {
        id <- bson.getAs[BSONObjectID]("_id")
        pass <- bson.getAs[String]("pass")
        name <- bson.getAs[String]("name")
      } yield AuthUser(id, name, pass)
      opt.get
    }
  }

  implicit object UserWriter extends BSONDocumentWriter[AuthUser] {
    def write(user: AuthUser): BSONDocument = {
      BSONDocument(
        "_id" -> user.id,
        "name" -> BSONString(user.name),
        "pass" -> BSONString(user.pass)
      )
    }
  }

  class AuthUserDAO extends GenericDAO[AuthUser] {

    def getUserByName(name: String, pws: String) : Future[Option[AuthUser]] = {
      headOption(BSONDocument("$and" -> BSONArray(BSONDocument("name" -> name), BSONDocument("pass" -> pws))))
    }

  }

  lazy val userDao = new AuthUserDAO

}

trait AuthUserDAO {
  def dao = AuthUserDAO.userDao
}
