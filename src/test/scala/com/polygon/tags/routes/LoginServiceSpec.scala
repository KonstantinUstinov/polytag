package com.polygon.tags.routes

import akka.event.Logging
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import spray.json.{JsonParser, ParserInput}
import akka.http.scaladsl.model.StatusCodes._
import com.polygon.tags.dao.AuthUser
import reactivemongo.bson.BSONObjectID
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}


class LoginServiceSpec extends FlatSpec
  with Matchers
  with ScalatestRouteTest
  with LoginService
  with BeforeAndAfterAll {

  override def afterAll: Unit = {

  }

  override def beforeAll: Unit ={

  }

  override implicit val executor: ExecutionContextExecutor = system.dispatcher
  override val logger = Logging(system, getClass)

  val appId = "poly-api"
  val appSecret = "123"

  val redirectUris = "https://localhost:20888/authorized"

  val userName = "John"
  val userPass = "snow"

  val id = BSONObjectID.generate()
  val wrongCredentials = BasicHttpCredentials(userName, "")
  val userCredentials = BasicHttpCredentials(userName, userPass)

  "Login Service" should "reject if wrong creds" in {

      Post(s"/login?response_type=code&client_id=$appId&client_secret=$appSecret&redirect_uri=$redirectUris&state=somestate") ~>
        addCredentials(wrongCredentials) ~> loginServiceRoute ~> check {
        status shouldBe Unauthorized
        JsonParser(ParserInput(responseAs[String])).asJsObject.fields("error").toString() === "invalid_client"
      }
    }

  "Login Service" should "return code" in {

    val f = user_dao.save(AuthUser(id, userName, userPass))
    Await.result(f, 10.second)

    Post(s"/login?response_type=code&client_id=$appId&client_secret=$appSecret&redirect_uri=$redirectUris&state=somestate") ~>
      addCredentials(userCredentials) ~> loginServiceRoute ~> check {
      status shouldBe OK
      JsonParser(ParserInput(responseAs[String])).asJsObject.fields("state").toString() === "somestate"
      JsonParser(ParserInput(responseAs[String])).asJsObject.fields("code").toString() !== ""
    }

  }

}
