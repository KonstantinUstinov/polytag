package com.polygon.tags.dao

import reactivemongo.bson.BSONObjectID

case class AuthUser(id: BSONObjectID, name: String, pass: String)