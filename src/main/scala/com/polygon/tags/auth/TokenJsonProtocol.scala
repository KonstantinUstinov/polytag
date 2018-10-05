package com.polygon.tags.auth

import spray.json.{JsObject, JsString, JsValue, RootJsonFormat}

import scalaoauth2.provider.OAuthError

case class CodeResponse(code: String, state: Option[String])

object TokenJsonProtocol {

  implicit object CodeResponseFormat extends RootJsonFormat[CodeResponse] {
    def write(t: CodeResponse) = JsObject(
      "code" -> JsString(t.code),
      "state" -> JsString(t.state.getOrElse(""))
    )

    def read(value: JsValue) = ???
  }

  implicit object OAuthErrorFormat extends RootJsonFormat[OAuthError] {
    def write(t: OAuthError) = JsObject(
      "error" -> JsString(t.errorType),
      "description" -> JsString(t.description)
    )

    def read(value: JsValue) = ???
  }

}
