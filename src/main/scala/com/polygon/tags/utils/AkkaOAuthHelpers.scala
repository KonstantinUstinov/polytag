package com.polygon.tags.utils

import akka.http.scaladsl.server.RequestContext
import scalaoauth2.provider.AuthorizationRequest


object AkkaOAuthHelpers {

  implicit def requestContextToAuthorizationRequest(requestContext: RequestContext): AuthorizationRequest = {
    val headers = requestContext.request.headers.map(h => h.name -> h.value).foldLeft(Map.empty[String, List[String]])((a, e) => a + (e._1 -> (e._2 :: a.getOrElse(e._1, List.empty))))
    val params = requestContext.request.uri.query().toMultiMap
    new AuthorizationRequest(headers, params)
  }

}
