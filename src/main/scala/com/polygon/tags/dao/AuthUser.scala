package com.polygon.tags.dao

case class AuthUser(name: String, pass: String, ssoToken: Option[String] = None)