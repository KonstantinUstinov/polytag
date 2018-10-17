package com.polygon.tags.routes

import akka.event.Logging
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import spray.json.{JsonParser, ParserInput}
import akka.http.scaladsl.model.StatusCodes._
import com.polygon.tags.dao.Domain
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.ContentTypes._
import reactivemongo.bson.BSONObjectID

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class DomainServiceSpec extends FlatSpec
  with Matchers
  with ScalatestRouteTest
  with DomainService
  with BeforeAndAfterAll {

  override implicit val executor: ExecutionContextExecutor = system.dispatcher
  override val logger = Logging(system, getClass)


  override def afterAll: Unit = {
    val f = domain_dao.removeAll()
    Await.result(f, 10.second)

    val list = List(
    domain_dao.save(Domain(BSONObjectID.generate(), "https://s.cubiqads.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://appfunnelads.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://arionads.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://asgardads.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://centaurmob.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://closedloopads.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://converpath.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://cubiqads.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://midfunnelads.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://nymeriasol.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://topfunnelads.com/api/tags")),
    domain_dao.save(Domain(BSONObjectID.generate(),"https://ygenads.com/api/tags")))

    Await.result(Future.sequence(list), 10.second)

    system.terminate()
  }

  override def beforeAll: Unit ={
    val f = domain_dao.removeAll()
    Await.result(f, 10.second)
  }

  "DomainService" should "Save daomain" in {

    Post(s"/domains?domain_uri=doors.com") ~>
      domainServiceRoute ~> check {
      status shouldBe OK
    }

  }

  "DomainService" should "Get all daomaind" in {

    Get(s"/domains") ~>
      domainServiceRoute ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[List[Domain]].count(_ => true) shouldBe 1
    }

  }

  "DomainService" should "update Domain" in {
    Get(s"/domains") ~>
      domainServiceRoute ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[List[Domain]].count(_ => true) shouldBe 1

      val id = responseAs[List[Domain]].head.id

      Put(s"/domains/${id.stringify}?domain_uri=doors.com.ua") ~> domainServiceRoute ~> check {
        status shouldBe OK

        Get(s"/domains") ~>
          domainServiceRoute ~> check {
          status shouldBe OK
          contentType shouldBe `application/json`
          responseAs[List[Domain]].count(_.path == "doors.com.ua") shouldBe 1
        }
      }
    }
  }

  "DomainService" should "delete Domain" in {

    Get(s"/domains") ~>
      domainServiceRoute ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[List[Domain]].count(_ => true) shouldBe 1

      val id = responseAs[List[Domain]].head.id
      Delete(s"/domains/${id.stringify}?domain_uri=doors.com.ua") ~> domainServiceRoute ~> check {
        status shouldBe OK

        Get(s"/domains") ~>
          domainServiceRoute ~> check {
          status shouldBe NotFound
        }
      }
    }
  }


}
