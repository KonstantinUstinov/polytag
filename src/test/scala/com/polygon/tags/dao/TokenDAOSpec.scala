package com.polygon.tags.dao

import java.util.Date

import com.polygon.tags.utils.ConfigProvider
import org.scalatest.AsyncFlatSpec
import reactivemongo.bson.{BSONDateTime, BSONObjectID}

class TokenDAOSpec extends AsyncFlatSpec with ConfigProvider with UserTokenDAO {

  val id = BSONObjectID.generate()

  val token1 = TokenDto(id.stringify, Some("456"), None, Some(1000L), BSONDateTime(new Date().getTime),
    TokenUserDto("name", None), Some("client-id"), None, Some("https://localhost:8021")
  )

  "TokenDAO" should "save tocken" in {
    tokenDao.save(token1).map{wr =>
      assert(wr.ok)
    }
  }

  "TokenDAO" should "get tocken" in {
    tokenDao.findByToken(id.stringify).map{wr =>
      assert(wr.get.token == id.stringify)
      assert(wr.get.redirectUri.get == "https://localhost:8021")
    }
  }

  "TokenDAO" should "delete tocken" in {
    tokenDao.removeByToken(id.stringify).map{wr =>
      assert(wr.ok)
    }
  }

  "TokenDAO" should "get tocken not exists" in {
    tokenDao.findByToken(id.stringify).map{wr =>
      assert(wr.isEmpty)
    }
  }

}
