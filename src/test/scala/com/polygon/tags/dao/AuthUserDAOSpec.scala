package com.polygon.tags.dao

import com.polygon.tags.utils.ConfigProvider
import org.scalatest.AsyncFlatSpec
import reactivemongo.bson.BSONObjectID

class AuthUserDAOSpec extends AsyncFlatSpec with ConfigProvider with AuthUserDAO {

  val id = BSONObjectID.generate()

  "AuthUserDAO" should "save user" in {
    val result = user_dao.save(AuthUser(id, "poly", "original"))
    result.map{wr =>
      assert(wr.ok)
    }
  }

  "AuthUserDAO" should "read user" in {
    user_dao.getUserByName("poly", "original").map {user =>
      assert(user.get.name == "poly")
      assert(user.get.pass == "original")
    }
  }

  "AuthUserDAO" should "read  Empty user" in {
    user_dao.getUserByName("poly", "original2").map {user =>
      assert(user.isEmpty)
    }
  }


  "AuthUserDAO" should "remove user" in {
    user_dao.remove(id).map{wr =>
      assert(wr.ok)
    }
  }
}
