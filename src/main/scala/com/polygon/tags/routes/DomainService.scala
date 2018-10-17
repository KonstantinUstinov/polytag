package com.polygon.tags.routes

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives.{complete, get, onComplete, parameters, pathPrefix, post, respondWithHeaders}
import com.polygon.tags.dao.{Domain, DomainDAO}
import reactivemongo.bson.BSONObjectID
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.polygon.tags.Protocols

import scala.concurrent.ExecutionContextExecutor
import scala.util.Success

trait DomainService extends  SprayJsonSupport with DomainDAO with Protocols {

  implicit val system: ActorSystem
  implicit val materializer: Materializer
  implicit val executor: ExecutionContextExecutor
  implicit val logger: LoggingAdapter

  def domainServiceRoute =
    pathPrefix("domains") {
      post {
        addDomainRoute
      } ~
      get {
        getAllDomains
      } ~
        updateDomain ~
        deleteDoamin
    }

  case class GetByIdRequest(id: String) {
    require(BSONObjectID.parse(id).isSuccess, "the informed id is not a representation of a valid hex string")
  }

  def addDomainRoute = parameters("domain_uri") { domain_url =>
    respondWithHeaders(RawHeader("Content-Type", "application/json")) {
      onComplete(domain_dao.save(Domain(BSONObjectID.generate(), domain_url))) {
        case Success(wr) =>
          if(wr.ok)
            complete(StatusCodes.OK -> "Successfully  Saved Domain")
          else
            complete(StatusCodes.InternalServerError -> wr.writeErrors.mkString(","))
        case _ =>
          complete(StatusCodes.InternalServerError -> "Cannot save Domain")
      }
    }
  }

  def getAllDomains =
    respondWithHeaders(RawHeader("Content-Type", "application/json")) {
      onComplete(domain_dao.getAll) {
        case Success(list) =>
          if(list.isEmpty)
            complete(StatusCodes.NotFound -> "Cannot return Domain")
          else
            complete(StatusCodes.OK -> list)
        case _ =>
          complete(StatusCodes.InternalServerError -> "Cannot return Domain")
      }
    }

  def updateDomain =
    (put & path(Segment).as(GetByIdRequest)) { request =>
      parameters("domain_uri") { domain_url =>
        onComplete(
          domain_dao.update(Domain(BSONObjectID.parse(request.id).get, domain_url))
        ) { result =>
          if(result.isSuccess)
            complete(StatusCodes.OK -> "Successfully  Saved Domain")
          else
            complete(StatusCodes.InternalServerError -> "Cannot update Domain")
        }
      }
    }

  def deleteDoamin =
    (delete & path(Segment).as(GetByIdRequest)) { request =>
      onComplete(domain_dao.remove(BSONObjectID.parse(request.id).get)) { result =>
        if(result.isSuccess)
          complete(StatusCodes.OK -> "Successfully  Delete Domain")
        else
          complete(StatusCodes.InternalServerError -> "Cannot delete Domain")
      }
    }
}
