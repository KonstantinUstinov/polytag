package com.polygon.tags.routes

import akka.event.Logging
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.polygon.tags.dao.{DSPTemplates, Tag}
import com.polygon.tags.routes.Service.{NewTag, UpdateTag}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import reactivemongo.bson.{BSONDateTime, BSONObjectID}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}

class ServiceSpec
  extends FlatSpec
    with Matchers
    with ScalatestRouteTest
    with Service
    with BeforeAndAfterAll {

  override implicit val executor: ExecutionContextExecutor = system.dispatcher
  override val logger = Logging(system, getClass)
  val id = BSONObjectID.generate()
  val tag = Tag(id, "poly", "original", "name", BSONDateTime(1535747635000L), BSONDateTime(1535747635000L), List("id-1", "id-2"), "domain", List(DSPTemplates.Nuviad, DSPTemplates.GetIntent))

  override def afterAll: Unit = {
    val f = dao.remove(id)
    Await.result(f, 10.second)
    system.terminate()
  }

  override def beforeAll: Unit ={
    val f = dao.remove(id)
    Await.result(f, 10.second)
  }

  "Service" should "save tag" in {
    Post(s"/api/tags", tag)  ~> routes ~> check {
      status shouldBe OK
    }
  }

  "Service" should "return TemporaryRedirect when name of tag in db" in {
    Post(s"/api/tags", tag)  ~> routes ~> check {
      status shouldBe Conflict
    }
  }

  "Service" should "get tag" in {

    Get(s"/api/tags/" + id.stringify) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[Tag].id shouldBe tag.id
      responseAs[Tag].name shouldBe tag.name
    }
  }

  "Service" should "return NotFound" in {

    Get(s"/api/tags/5b89a65401000001009f6789") ~> routes ~> check {
      status shouldBe NotFound
    }
  }

  "Service" should "return invalid id on get" in {
    Get(s"/api/tags/1") ~> routes ~> check {
      rejection.toString shouldEqual "ValidationRejection(requirement failed: the informed id is not a representation of a valid hex string,Some(java.lang.IllegalArgumentException: requirement failed: the informed id is not a representation of a valid hex string))"
    }
  }

  "Service" should "generate tag" in {
    Post(s"/api/tags/generate", NewTag("name23", "<div id=\"video 1037061381[CB]\" style=\"width: 300px; height: 250px;\">\n\n  <script src=\"https://p.algovid.com/player/player.js?p=1037061381&sid=[APP_BUNDLE]&cb=[CB]&appn=[APP_NAME]&appv=[APP_VER]&appb=[APP_BUNDLE]&appsu=[APP_STORE_URL]&appidfa=[APP_IDFA]&appaid=[APP_AID]&appsi=[APP_STORE_ID]&appc=[APP_CATEGORY]&country=[COUNTRY_ID]&loc=[LOCATION]&loclong=[LOCATION_LONG]&loclat=[LOCATION_LAT]&deviceid=[DEVICEID]&dnt=[DNT]&w=300&h=250&d=[APP_BUNDLE]\"\n\n    type=\"text/javascript\"></script>")) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      val tag = responseAs[Tag]
      println(tag.polyTag)
      tag.playerIDs shouldBe List("1037061381")
      tag.DSPs shouldBe List(DSPTemplates.Nuviad)
    }
  }

   "Service" should "return polytag" in {
    Get(s"/api/tags/original?p=" + id.stringify + "&sid=[[BUNDLE_ID_ENCODED]]&d=[[BUNDLE_ID_ENCODED]]&appn=[[APP_NAME_ENCODED]") ~> routes ~> check {
      status shouldBe OK
      responseAs[String] shouldBe "original"
    }
  }

  "Service" should "return not found polytag" in {
    Get(s"/api/tags/original?p=" + BSONObjectID.generate().stringify + "&sid=[[BUNDLE_ID_ENCODED]]&d=[[BUNDLE_ID_ENCODED]]&appn=[[APP_NAME_ENCODED]") ~> routes ~> check {
      status shouldBe NotFound
    }
  }

  "Service" should "search by tag id" in {
    Get(s"/api/tags/search?polytagid=" + id.stringify) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[List[Tag]].count(_ => true) shouldBe 1
    }
  }

  "Service" should "update  tag" in {
    Put(s"/api/tags/" + id.stringify, UpdateTag("polygon", "origale", "Name2", List(), "domain", List(DSPTemplates.GetIntent))) ~> routes ~> check {
      status shouldBe OK
    }

    Get(s"/api/tags/" + id.stringify) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[Tag].id shouldBe tag.id
      responseAs[Tag].name shouldBe "Name2"
      responseAs[Tag].originalTag shouldBe "origale"
      responseAs[Tag].polyTag shouldBe "polygon"
      responseAs[Tag].DSPs shouldBe List(DSPTemplates.GetIntent)
      responseAs[Tag].playerIDs shouldBe List()
    }
  }

  "Service" should "not return error" in {
    Get("/api/tags/original?p=" + id.stringify + "&sid=$%7BAPP_BUNDLE_ID%7D&cb=$%7BCACHE_BUSTER%7D&appn=$%7BAPP_NAME%7D&appb=$%7BAPP_BUNDLE_ID%7D&appsu=$%7BAPP_STORE_URL%7D&appidfa=$%7BIOS_IFA%7D&appaid=$%7BGOOGLE_AID%7D&appsi=$%7BAPP_BUNDLE_ID%7D&loclong=$%7BLONGITUDE%7D&loclat=$%7BLATITUDE%7D&w=480&h=320&ho=1&d=$%7BAPP_BUNDLE_ID%7D&c4=$%7BIMPRESSION_CONTEXT%7D&c5=$%7BIMPRESSION_CONTEXT%7D") ~> routes ~> check {
      status shouldBe OK
    }
  }

  "Service" should "return  object js" in {
    Get(s"/api/tags/object?p=" + id.stringify + "&sid=[[BUNDLE_ID_ENCODED]]&d=[[BUNDLE_ID_ENCODED]]&appn=[[APP_NAME_ENCODED]") ~> routes ~> check {
      status shouldBe OK
      responseAs[String] shouldBe s""" document.write('<object id="object" type="text/html"  data="http://example.com/api/tags/original?p=${id.stringify}&sid=[[BUNDLE_ID_ENCODED]]&d=[[BUNDLE_ID_ENCODED]]&appn=[[APP_NAME_ENCODED]" style="width: 320px; height: 270px;"><p>backup content</p></object>'); """
    }
  }

  "Service" should "delete  tag" in {
    Delete(s"/api/tags/" + id.stringify ) ~> routes ~> check {
      status shouldBe OK
    }

    Get(s"/api/tags/original?p=" + id.stringify + "&sid=[[BUNDLE_ID_ENCODED]]&d=[[BUNDLE_ID_ENCODED]]&appn=[[APP_NAME_ENCODED]") ~> routes ~> check {
      status shouldBe NotFound
    }
  }

}