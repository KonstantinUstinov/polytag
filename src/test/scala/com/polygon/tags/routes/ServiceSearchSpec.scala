package com.polygon.tags.routes

import akka.event.Logging
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.polygon.tags.dao.{DSPTemplates, Tag}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDateTime, BSONObjectID}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.DateTime

class ServiceSearchSpec   extends FlatSpec
  with Matchers
  with ScalatestRouteTest
  with Service
  with BeforeAndAfterAll {

  override implicit val executor: ExecutionContextExecutor = system.dispatcher
  override val logger = Logging(system, getClass)


  val id = BSONObjectID.generate()
  val tag = Tag(id, "poly", "original", "name", BSONDateTime(1535747635000L), BSONDateTime(1535747635000L), List("id-1", "id-2"), List(DSPTemplates.Nuviad, DSPTemplates.GetIntent))

  val id2 = BSONObjectID.generate()
  val tag2 = Tag(id2, "poly2", "original2", "name2", BSONDateTime(1535747635000L), BSONDateTime(1535747635000L), List("id-22", "id-33"), List(DSPTemplates.Peak226, DSPTemplates.Appreciate))

  private def joinFuture(f1: Future[WriteResult], f2: Future[WriteResult]) = {
    for {
      a <- f1
      b <- f2
    } yield List(a, b)
  }

  override def afterAll: Unit = {
    Await.result(joinFuture(dao.remove(id), dao.remove(id2)), 10.second)
    system.terminate()
  }

  override def beforeAll: Unit ={
    Await.result(joinFuture(dao.remove(id), dao.remove(id2)), 10.second)
  }

  "Service" should "save tags" in {

    Post(s"/api/tags", tag)  ~> routes ~> check {
      status shouldBe OK
    }

    Post(s"/api/tags", tag2)  ~> routes ~> check {
      status shouldBe OK
    }
  }

  "Search" should "be path by id" in {
    Get(s"/api/tags/search?polytagid=" + id.stringify) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[List[Tag]].count(_ => true) shouldBe 1
    }
  }

  "Search" should "be path by playerID" in {
    Get(s"/api/tags/search?playerid=id-22") ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[List[Tag]].count(_ => true) shouldBe 1
    }
  }

  "Search" should "be path by DSP" in {
    Get(s"/api/tags/search?dsp=Peak226&name=name2") ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[List[Tag]].count(_ => true) shouldBe 1
    }
  }

  "Search" should "be path by name" in {
    Get(s"/api/tags/search?name=name2") ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[List[Tag]].count(_ => true) shouldBe 1
    }
  }

  "Search" should "be path by creation date" in {
    val date = DateTime(BSONDateTime(System.currentTimeMillis()).value).toIsoDateTimeString()
    Get(s"/api/tags/search?creationdatefrom=" + date + "&creationdateto=" + date + "&polytagid=" + id.stringify) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[List[Tag]].count(_ => true) shouldBe 1
    }

    val date2 = DateTime(BSONDateTime(System.currentTimeMillis()).value)

    Get(s"/api/tags/search?creationdatefrom=" + DateTime(date2.year, date2.month, date2.day + 1).toIsoDateTimeString() + "&creationdateto=" + DateTime(date2.year, date2.month, date2.day + 1).toIsoDateTimeString()) ~> routes ~> check {
      status shouldBe NotFound

    }
  }


  "Search" should "be path by updatedate date" in {
    val date = DateTime(BSONDateTime(System.currentTimeMillis()).value).toIsoDateTimeString()
    Get(s"/api/tags/search?updatedatefrom=" + date + "&polytagid=" + id2.stringify + "&updatedateto=" + date) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[List[Tag]].count(_ => true) shouldBe 1
    }

    val date2 = DateTime(BSONDateTime(System.currentTimeMillis()).value)

    Get(s"/api/tags/search?updatedatefrom=" + DateTime(date2.year, date2.month, date2.day + 1).toIsoDateTimeString() + "&updatedateto=" + DateTime(date2.year, date2.month, date2.day + 1).toIsoDateTimeString()) ~> routes ~> check {
      status shouldBe NotFound

    }
  }
}
