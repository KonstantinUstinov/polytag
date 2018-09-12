package com.polygon.tags.dao

import reactivemongo.api.Cursor

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID}

import scala.concurrent.Future

class GenericDAO[T: BSONDocumentReader : BSONDocumentWriter](implicit ctx: MongoDriverContext[T]) {

  def save(doc: T): Future[WriteResult] = {
    ctx.collection.map(_.insert(doc)).flatten
  }

  def update(query: BSONDocument, updates: BSONDocument, upsert: Boolean ): Future[UpdateWriteResult] = {
    ctx.collection.map(_.update(query, updates, upsert = upsert)).flatten
  }

  def headOption(query: BSONDocument, sort: BSONDocument = BSONDocument()): Future[Option[T]] = {
    ctx.collection.map(_.find(query).sort(sort).one[T]).flatten
  }

  def find(query: BSONDocument, sort: BSONDocument = BSONDocument(), limit: Int = Int.MaxValue) : Future[List[T]] = {
    ctx.collection.map(_.find(query).sort(sort).cursor[T]().collect[List](limit, Cursor.FailOnError[List[T]]())).flatten
  }

  def findById(id: BSONObjectID): Future[Option[T]] = {
    ctx.collection.map(_.find(BSONDocument("_id" -> id)).one[T]).flatten
  }

  def removeAll(): Future[WriteResult] = {
    ctx.collection.map(_.remove(BSONDocument())).flatten
  }

  def remove(id: BSONObjectID): Future[WriteResult] = {
    ctx.collection.map(_.remove(BSONDocument("_id" -> id))).flatten
  }

}
