package com.polygon.tags.dao

import com.polygon.tags.utils.ConfigProvider
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID, BSONString}

case class Domain(id: BSONObjectID, path: String)

object DomainDAO extends ConfigProvider {

  import MongoDriverContext.mongoExecutionContext
  implicit lazy val userContext = MongoDriverContext[Domain](config, "mongo.domain")

  implicit object UserReader extends BSONDocumentReader[Domain] {
    def read(bson: BSONDocument): Domain = {
      val opt = for {
        id <- bson.getAs[BSONObjectID]("_id")
        path <- bson.getAs[String]("path")
      } yield Domain(id, path)
      opt.get
    }
  }

  implicit object UserWriter extends BSONDocumentWriter[Domain] {
    def write(domain: Domain): BSONDocument = {
      BSONDocument(
        "_id" -> domain.id,
        "path" -> BSONString(domain.path)
      )
    }
  }

  class DomainDAO extends GenericDAO[Domain] {

    def getAll = {
      find(BSONDocument())
    }

    def update(domain: Domain) = {
      super.update(BSONDocument("_id" -> domain.id), BSONDocument("$set" -> BSONDocument("path" -> domain.path)),
        upsert = false)
    }

  }

  lazy val domainDao = new DomainDAO
}


trait  DomainDAO {
  def domain_dao = DomainDAO.domainDao
}