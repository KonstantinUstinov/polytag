package com.polygon.tags

import akka.http.scaladsl.model.DateTime
import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsObject, JsString, JsValue, RootJsonFormat}
import com.polygon.tags.dao.{DSPTemplates, Tag}
import com.polygon.tags.routes.Service.{ErrorDetail, NewTag, UpdateTag}
import reactivemongo.bson.{BSONDateTime, BSONObjectID}

trait Protocols extends DefaultJsonProtocol {

  implicit val opUpdateTagFormat: RootJsonFormat[UpdateTag] = new RootJsonFormat[UpdateTag] {
    override def write(obj: UpdateTag): JsValue = {
      JsObject(Map[String, JsValue](
        "polyTag" -> JsString(obj.polyTag),
        "originalTag" -> JsString(obj.originalTag),
        "name" -> JsString(obj.name),
        "playerIDs" -> JsArray(obj.playerIDs.map(JsString(_)).toVector),
        "DSPs" -> JsArray(obj.DSPs.map(t => JsString(t.toString)).toVector)
      ))
    }

    override def read(json: JsValue): UpdateTag =
      json.asJsObject.getFields("polyTag", "originalTag", "name", "playerIDs", "DSPs") match {
        case Seq(JsString(polyTag), JsString(originalTag), JsString(name), JsArray(playerIDs), JsArray(dps)) =>
          UpdateTag(polyTag,
            originalTag,
            name,
            playerIDs.map(_.convertTo[String]).toList,
            dps.map(dps => DSPTemplates.withName(dps.convertTo[String])).toList)
        case _ => throw new DeserializationException("UpdateTag expected")
      }
  }

  implicit val opTagFormat: RootJsonFormat[Tag] = new RootJsonFormat[Tag] {
    override def write(obj: Tag): JsValue = {
      JsObject(Map[String, JsValue](
        "id" -> JsString(obj.id.stringify),
        "polyTag" -> JsString(obj.polyTag),
        "originalTag" -> JsString(obj.originalTag),
        "name" -> JsString(obj.name),
        "creationDate" -> JsString(DateTime(obj.creationDate.value).toIsoDateTimeString()),
        "modifiedDate" -> JsString(DateTime(obj.modifiedDate.value).toIsoDateTimeString()),
        "playerIDs" -> JsArray(obj.playerIDs.map(JsString(_)).toVector),
        "DSPs" -> JsArray(obj.DSPs.map(t => JsString(t.toString)).toVector)
      ))
    }

    override def read(json: JsValue): Tag =
      json.asJsObject.getFields("id", "polyTag", "originalTag", "name", "creationDate", "modifiedDate", "playerIDs", "DSPs") match {
        case Seq(JsString(id), JsString(polyTag), JsString(originalTag), JsString(name), JsString(creationDate), JsString(modifiedDate), JsArray(playerIDs), JsArray(dps)) =>
          Tag(BSONObjectID.parse(id).get,
              polyTag,
              originalTag,
              name,
              BSONDateTime(DateTime.fromIsoDateTimeString(creationDate).get.clicks),
              BSONDateTime(DateTime.fromIsoDateTimeString(modifiedDate).get.clicks),
              playerIDs.map(_.convertTo[String]).toList,
              dps.map(dps => DSPTemplates.withName(dps.convertTo[String])).toList)
        case _ => throw new DeserializationException("tag expected")
      }

  }

  implicit val errorDetailFormat = jsonFormat4(ErrorDetail.apply)
  implicit val newTag = jsonFormat2(NewTag.apply)

}