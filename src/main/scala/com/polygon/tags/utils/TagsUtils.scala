package com.polygon.tags.utils

import java.io.StringReader
import akka.http.scaladsl.model.Uri
import nu.validator.htmlparser.common.XmlViolationPolicy
import nu.validator.htmlparser.sax.HtmlParser
import org.xml.sax.InputSource
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

  private def getRootNode(tag: String): List[Node] = {
    val hp = new HtmlParser
    hp.setNamePolicy(XmlViolationPolicy.ALLOW)

    val saxer = new NoBindingFactoryAdapter
    hp.setContentHandler(saxer)
    hp.parse(new InputSource(new StringReader(tag)))

    (saxer.rootElem \\ "div" \\ "script").toList
  }

}
