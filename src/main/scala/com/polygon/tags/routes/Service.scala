package com.polygon.tags.routes

import akka.http.scaladsl.server.Directives._
import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.server
import akka.stream.Materializer
import com.polygon.tags.Protocols
import com.polygon.tags.dao.{DSPTemplates, Tag, TagDAO}
import com.polygon.tags.routes.Service.{ErrorDetail, NewTag, UpdateTag}
import com.polygon.tags.utils.{ConfigProvider, TagsUtils}
import reactivemongo.bson.{BSONArray, BSONDateTime, BSONDocument, BSONObjectID, BSONString}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.StandardRoute
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.HttpCharsets._
import reactivemongo.bson

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

object Service {
  case class ErrorDetail(code: Int, error: String, message: Option[String] = None, info: Option[String] = None)
  case class NewTag(name: String, original: String, dsp: String = "Nuviad")
  case class UpdateTag(polyTag: String, originalTag: String, name: String, playerIDs: List[String], DSPs: List[DSPTemplates.DSPTemplates])
}

trait Service extends Protocols with ConfigProvider with TagDAO {

  implicit val system: ActorSystem
  implicit val materializer: Materializer
  implicit val executor: ExecutionContextExecutor
  implicit val logger: LoggingAdapter

  case class FindByIdRequest(id: String) {
    require(BSONObjectID.parse(id).isSuccess, "the informed id is not a representation of a valid hex string")
  }

  case class SearchRequest(_id: Option[String], playerIDs: Option[String], creationDateFrom: Option[String], creationDateTo: Option[String], modifiedDateFrom: Option[String], modifiedDateTo: Option[String], DSPs: Option[String], name: Option[String], limit: Option[Int]) {
    if(_id.nonEmpty)
      require(BSONObjectID.parse(_id.get).isSuccess, "the informed id is not a representation of a valid hex string")

    if(creationDateFrom.nonEmpty)
      require(DateTime.fromIsoDateTimeString(creationDateFrom.get).nonEmpty, "the informed datetime is not a representation of a valid IsoDateTimeString")

    if(creationDateTo.nonEmpty)
      require(DateTime.fromIsoDateTimeString(creationDateTo.get).nonEmpty, "the informed datetime is not a representation of a valid IsoDateTimeString")

    if(modifiedDateFrom.nonEmpty)
      require(DateTime.fromIsoDateTimeString(modifiedDateFrom.get).nonEmpty, "the informed datetime is not a representation of a valid IsoDateTimeString")

    if(modifiedDateTo.nonEmpty)
      require(DateTime.fromIsoDateTimeString(modifiedDateTo.get).nonEmpty, "the informed datetime is not a representation of a valid IsoDateTimeString")

    if(DSPs.nonEmpty)
      require( DSPTemplates.values.exists(_.toString == DSPs.get), "the informed dsp template is not a representation of a valid dsp" )
  }

  def assets = {
    def redirectSingleSlash =
      pathSingleSlash {
        get {
          redirect("index.html", PermanentRedirect)
        }
      }
    getFromResourceDirectory("web") ~ redirectSingleSlash
  }

  val routes = assets ~
    pathPrefix("api" / "tags") {
      (get & path(Segment).as(FindByIdRequest)) { request =>
        onComplete(dao.findById(BSONObjectID.parse(request.id).get)) { result =>
          futureHandler(result)
        }
      } ~
      (post & pathEndOrSingleSlash) {
        entity(as[Tag]) { item =>
          onComplete(dao.insertIfNotExists(item.copy(creationDate = BSONDateTime(System.currentTimeMillis()),
                                                     modifiedDate = BSONDateTime(System.currentTimeMillis())))) { result =>
            futureHandler(result)
          }
        }
      } ~
      (put & path(Segment).as(FindByIdRequest)) { request =>
        entity(as[UpdateTag]) { tag =>
            onComplete(
                dao.update(BSONDocument("_id" -> BSONObjectID.parse(request.id).get), BSONDocument("$set" -> BSONDocument("name" -> tag.name,
                                                                                                                          "polyTag" -> tag.polyTag,
                                                                                                                          "originalTag" -> tag.originalTag,
                                                                                                                          "playerIDs" -> BSONArray(tag.playerIDs.map(BSONString(_))),
                                                                                                                          "DSPs" -> BSONArray(tag.DSPs.map(t => BSONString(t.toString))),
                                                                                                                          "modifiedDate" -> BSONDateTime(System.currentTimeMillis()))),
                  upsert = false)
          ){ result =>
            futureHandler(result)
          }
        }
      } ~
      (delete & path(Segment).as(FindByIdRequest)) { request =>
        onComplete(dao.remove(BSONObjectID.parse(request.id).get)) { result =>
          futureHandler(result)
        }
      } ~
      pathPrefix("generate") {
        (post & pathEndOrSingleSlash) {
          entity(as[NewTag]) { tag =>
            onComplete(dao.findByName(tag.name).map {
              case Some(_) => complete(Conflict, ErrorDetail(409, "Name of tag already exists"))
              case None =>
                val players =  TagsUtils.getPlayerIDs(tag.original)
                val dsp = TagsUtils.getDSPTemplates(tag.original)
                val id = BSONObjectID.generate()
                if(players.isEmpty & dsp.isEmpty)
                  complete(Conflict, ErrorDetail(409, "cannot parse players id"))
                else
                  complete(OK, Tag(id, generatePolytag(id, tag.dsp), tag.original, tag.name, BSONDateTime(System.currentTimeMillis()), BSONDateTime(System.currentTimeMillis()), players, List(DSPTemplates.withName(tag.dsp))))
            }){ result =>
              futureHandler(result)
            }
          }
        }
      } ~
      pathPrefix("original") {
        get {
          parameters('p.as[String]).as(FindByIdRequest) { p =>
            onComplete(dao.getPolyTag(BSONObjectID.parse(p.id).get)) { result =>
              futureHandler(result)
            }
          }
        }
      } ~
      pathPrefix("object") {
        get {
          parameters('p.as[String]).as(FindByIdRequest) { p =>
            complete {
              HttpEntity(`application/javascript` withCharset `UTF-8`,s""" document.write('<body style="overflow:hidden;"> <object id="object" type="text/html"  data="${config.getString("polytag_url")}/original?p=${p.id}" width="100%" height="100%"><p>backup content</p></object> </body>'); """)
             }
          }
        }
      } ~
      pathPrefix("search") {
        get {
          parameters('polytagid.as[String].?, 'playerid.as[String].?, 'creationdatefrom.as[String].?, 'creationdateto.as[String].?, 'updatedatefrom.as[String].?, 'updatedateto.as[String].?, 'dsp.as[String].?, 'name.as[String].?, 'limit.as[Int].?).as(SearchRequest) { request =>
            onComplete(dao.find(generateSearchJson(request),limit = request.limit.getOrElse(100))) { result =>
              futureHandler(result)
            }
          }
        }
      }
    } ~
    path("config") {
        complete {
          s"""|var GLOBAL_ENV_CONFIG = {
              |"service-mapping": {
              |  "api" : "https://s.cubiqads.com:80/api/tags"
              |  },
              |"editor-max-length": "65535",
              |"snippetDescMaxLength": "256",
              |"snippetPathMaxLength": "1024",
              |"timeOut": "30m" }""".stripMargin
        }
    }

  private def generateSearchJson(search: SearchRequest) : BSONDocument =  {
    val _id = search._id.map(id => BSONDocument(BSONDocument("_id" -> BSONObjectID.parse(id).get)))
    val playerID = search.playerIDs.map(id => BSONDocument("playerIDs" -> id))
    val dSP = search.DSPs.map(id => BSONDocument("DSPs" -> id))
    val name = search.name.map(name => BSONDocument("name" -> name))
    val creationDate = search.creationDateFrom.map(creationDate => BSONDocument("creationDate" -> BSONDocument("$gte" -> BSONDateTime(getCurrentDate(creationDate)), "$lt" -> BSONDateTime(getNextDate(search.creationDateTo.get)))))
    val modifiedDate = search.modifiedDateFrom.map(modifiedDate => BSONDocument("modifiedDate" -> BSONDocument("$gte" -> BSONDateTime(getCurrentDate(modifiedDate)), "$lt" -> BSONDateTime(getNextDate(search.modifiedDateTo.get)))))

    search match {
      case SearchRequest(None, None, None, None, None, None, None, None, _) => BSONDocument()
      case _ =>  BSONDocument("$and" -> BSONArray(List(_id, playerID, dSP, name, creationDate, modifiedDate).flatten))
    }
  }

  private def getCurrentDate(dateStr: String) : Long = {
    val date = DateTime.fromIsoDateTimeString(dateStr).get
    DateTime(date.year, date.month, date.day).clicks
  }

  private def getNextDate(dateStr: String) : Long = {
    val date = DateTime.fromIsoDateTimeString(dateStr).get
    DateTime(date.year, date.month, date.day + 1).clicks
  }

  private def generatePolytag(id: BSONObjectID, dsp: String): String = {
    s"""<div id="video${id.stringify}"></div>\n<script src="${config.getString("polytag_url")}/object?p=${id.stringify}&${config.getString(s"DSPtemplates.$dsp")}" \nType="text/javascript"></script>"""
  }

  val futureHandler: PartialFunction[Try[Any], server.Route] = {

    case Success(Some(tag: Tag))              =>
      complete(OK, tag)

    case Success(list :List[Tag])             =>
      if(list.isEmpty)
        complete(NotFound, ErrorDetail(404, "Not Found"))
      else
        complete(OK, list)

    case Success(None)                        =>
      complete(NotFound, ErrorDetail(404, "Not Found"))

    case Success(response: ErrorDetail)       =>
      complete(response.code, response)

    case Success(false)                       =>
      complete(Conflict, ErrorDetail(409, "Name of tag already exists"))

    case Success(true)                        =>
      complete(OK, "Tag created")

    case Success(Some(polytag: String))       =>
      //complete(HttpEntity(`application/javascript` withCharset `UTF-8`, polytag))
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, polytag))

    case Success(route: StandardRoute)        =>
      route

    case Success(uwr: UpdateWriteResult)      =>
      uwr.nModified match {
        case 1 => complete(200, "Tag updated")
        case _ =>   complete(InternalServerError, "Did not update")
      }

    case Success(uwr: WriteResult)      =>
      uwr.n match {
        case 1 => complete(200, "Tag deleted")
        case _ =>   complete(InternalServerError, "Did not deleted")
      }

    case Failure(e: Exception)                =>
      complete(InternalServerError, ErrorDetail(e.hashCode(), e.toString, Some(e.getMessage), Some(e.getLocalizedMessage)))

    case unknown: Any                         =>
      complete(InternalServerError, unknown.toString)

  }

}
