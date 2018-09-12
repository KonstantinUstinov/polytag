package com.polygon.tags.dao

import reactivemongo.bson.{BSONDateTime, BSONObjectID}

case class Tag(id: BSONObjectID,
               polyTag: String,
               originalTag: String,
               name: String,
               creationDate: BSONDateTime,
               modifiedDate: BSONDateTime,
               playerIDs: List[String],
               DSPs: List[DSPTemplates.DSPTemplates])

object DSPTemplates extends Enumeration {
  type DSPTemplates = Value
  val Nuviad, Peak226, Appreciate, GetIntent = Value
}