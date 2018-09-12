package com.polygon.tags.dao

import com.polygon.tags.utils.ConfigProvider
import com.typesafe.config.Config
import scala.collection.concurrent.TrieMap
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import reactivemongo.api.{MongoConnection, DefaultDB}
import reactivemongo.api.collections.bson.BSONCollection
import scala.concurrent.Future

object MongoDriverContext extends ConfigProvider {

  implicit lazy val mongoExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(config.getInt("mongo.channelsPerNode")))
  lazy val driver = new reactivemongo.api.MongoDriver

  private val connections = TrieMap.empty[String, MongoConnection]

  def connection(uri: String): MongoConnection = connections.getOrElseUpdate(uri,
    MongoConnection.parseURI(uri).map { parsedUri =>
      driver.connection(parsedUri)
    }.get
  )

}

case class MongoDriverContext[A](config: Config, dbPath: String)(implicit executionContext: ExecutionContext) {

  private val dbName = config.getString(dbPath + ".db")
  private val collectionName = config.getString(dbPath + ".collection")
  private val uri = config.getString(dbPath + ".uri")

  def db: Future[DefaultDB] = MongoDriverContext.connection(uri).database(dbName)
  lazy val collection: Future[BSONCollection] = db.map(_.apply(collectionName))
}
