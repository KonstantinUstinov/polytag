package com.polygon.tags.utils

import com.typesafe.config.ConfigFactory

trait ConfigProvider {
  lazy val config = ConfigFactory.load()
}
