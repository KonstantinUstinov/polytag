package com.polygon.tags.utils

import java.nio.charset.Charset
import org.apache.commons.codec.binary.Base64

object Crypto {

  val charset = Charset.forName("UTF-8")

  def generateToken(): String = {
    val key = java.util.UUID.randomUUID.toString
    new String(new Base64().encode(key.getBytes(charset)), charset)
  }

  def basicCredentials(username: String, password: String): String = {
    val userPass = username + ':' + password
    val cookie = new String(new Base64().encode(userPass.getBytes(charset)), charset)
    "Basic " + cookie
  }
}
