package com.polygon.tags.dao

import scala.concurrent.Future
import com.polygon.tags.utils.ConfigProvider
import reactivemongo.bson.{BSONArray, BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID, BSONString}

object TagDAO extends ConfigProvider {

  import MongoDriverContext.mongoExecutionContext
  implicit lazy val tagContext = MongoDriverContext[Tag](config, "mongo.tag")

  implicit object TagReader extends BSONDocumentReader[Tag] {
    def read(bson: BSONDocument): Tag = {
      val opt = for {
        id <- bson.getAs[BSONObjectID]("_id")
        polyTag <- bson.getAs[String]("polyTag")
        originalTag <- bson.getAs[String]("originalTag")
        name <- bson.getAs[String]("name")
        creationDate <- bson.getAs[BSONDateTime]("creationDate")
        modifiedDate <- bson.getAs[BSONDateTime]("modifiedDate")
        playerIDs <- bson.getAs[List[String]]("playerIDs")
        dsp <- bson.getAs[BSONArray]("DSPs")

      } yield Tag(id, polyTag, originalTag, name, creationDate, modifiedDate, playerIDs, convertArrayToDSP(dsp))
      opt.get
    }

    private def convertArrayToDSP(dps: BSONArray) : List[DSPTemplates.DSPTemplates] = {
      dps.toMap.values.map {
        case str: BSONString => DSPTemplates.withName(str.value)
        case _ => throw new Exception("Not valid data")
      }.toList
    }
  }

  implicit object TagWriter extends BSONDocumentWriter[Tag] {
    def write(tag: Tag): BSONDocument = {
      BSONDocument(
        "_id" -> tag.id,
        "polyTag" -> BSONString(tag.polyTag),
        "originalTag" -> BSONString(tag.originalTag),
        "name" -> BSONString(tag.name),
        "creationDate" -> tag.creationDate,
        "modifiedDate" -> tag.modifiedDate,
        "playerIDs" -> BSONArray(tag.playerIDs.map(BSONString(_))),
        "DSPs" -> BSONArray(tag.DSPs.map(t => BSONString(t.toString)))
      )
    }
  }

  class TagDAO extends GenericDAO[Tag] {

    def findByName(name: String): Future[Option[Tag]] = {
      headOption(BSONDocument("name" -> name))
    }

    def insertIfNotExists(tag: Tag): Future[Boolean] = {
      findByName(tag.name) flatMap {
        case Some(_) => Future.successful(false)
        case None => save(tag)  map {
          case _ => true
        }
      }
    }

    def getPolyTag(id: BSONObjectID) : Future[Option[String]] = {
      findById(id).map(_.map(_.originalTag))
    }

  }

  lazy val tagDao = new TagDAO

}

trait TagDAO {
  def dao = TagDAO.tagDao
}
