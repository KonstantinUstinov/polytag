package com.polygon.tags.dao

import java.util.Date

import reactivemongo.bson.{BSONDateTime, BSONObjectID}

import scalaoauth2.provider.{AccessToken, AuthInfo}

case class TokenUserDto(name: String, ssoToken: Option[String])

case class TokenDto(
                     // token fields
                     token: String,
                     refreshToken: Option[String],
                     scope: Option[String],
                     expiresIn: Option[Long],
                     createdAt: BSONDateTime,

                     // auth info fields
                     user: TokenUserDto,
                     clientId: Option[String],
                     authInfoScope: Option[String],
                     redirectUri: Option[String]
                   ) {

  def asAuthInfo: AuthInfo[AuthUser] = AuthInfo(AuthUser(BSONObjectID.generate(), user.name, ""), clientId, authInfoScope, redirectUri)

  def asToken: AccessToken = AccessToken(token, refreshToken, scope, expiresIn, new Date(createdAt.value))
}