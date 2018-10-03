package com.polygon.tags.utils

import java.io.StringReader
import java.util.regex.{Matcher, Pattern}

import akka.http.scaladsl.model.Uri
import nu.validator.htmlparser.common.XmlViolationPolicy
import nu.validator.htmlparser.sax.HtmlParser
import org.xml.sax.InputSource
import spray.json.JsString

import scala.xml.Node
import scala.xml.parsing.NoBindingFactoryAdapter

object TagsUtils {

  def getPlayerIDs(tag: String) : List[String] = {
    getRootNode(tag).
      flatMap(_.attribute("src")).
      flatten.flatMap(url => Uri(url.text).query().
      get("p"))
  }

  def getDSPTemplates(tag: String): List[Uri.Query] = {
    getRootNode(tag).
      flatMap(_.attribute("src")).
      flatten.map(
        url => Uri(url.text).query().filterNot { case (k, _) => k == "p" }
      )
  }

  def replaceQueryInTag(tag: String, query: Uri.Query) : String = {
    val q = query.filterNot { case (k, _) => k == "p" }
    getDSPTemplates(tag).foldLeft(tag)(
      (tag, uriQuery) =>
        tag.replaceAll(Pattern.quote(uriQuery.foldLeft("")((url, p) => url + "&" + p._1 + "=" + p._2)), Matcher.quoteReplacement("&" + q.toString()))
    )
  }

  def getSizeOfAd(tag: String) : List[(Int, Int)] = {
    getDivNode(tag).
      flatMap(_.attribute("style")).
      flatten.flatMap(style => convertStyleToSize(style.text))
  }

  def convertStyleToSize(style: String) : Option[(Int, Int)] = {
    val map = style.split(";").
      flatMap(v => convertArrayToJson(v.split(":"))).filter(e => (e._1 == "width") || (e._1 == "height")).toMap

    if (map.size != 2)
      None
    else
      Some(map("width").replaceAll("px", "").toInt, map("height").replaceAll("px", "").toInt)
  }

  private def convertArrayToJson(arr: Array[String]) : Option[(String, String)] = {
    if (arr.length != 2)
      None
    else
      Some(arr(0).trim, arr(1).trim)
  }

  private def getDivNode(tag: String): List[Node] = {
    (getBinder(tag).rootElem \\ "div" ).toList
  }

  private def getRootNode(tag: String): List[Node] = {
    (getBinder(tag).rootElem \\ "div" \\ "script").toList
  }

  private def getBinder(tag: String) : NoBindingFactoryAdapter = {
    val hp = new HtmlParser
    hp.setNamePolicy(XmlViolationPolicy.ALLOW)

    val saxer = new NoBindingFactoryAdapter
    hp.setContentHandler(saxer)
    hp.parse(new InputSource(new StringReader(tag)))
    saxer
  }

}
