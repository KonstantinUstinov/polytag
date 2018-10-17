package com.polygon.tags.dao

import com.polygon.tags.utils.ConfigProvider
import org.scalatest.AsyncFlatSpec
import scala.concurrent.Future
import reactivemongo.bson.{BSONDateTime, BSONObjectID}

class TagDAOSpec extends AsyncFlatSpec with ConfigProvider with TagDAO {

  val id = BSONObjectID.generate()
  val id2 = BSONObjectID.generate()

  "TagDAO" should "save tag empty list" in {

    val result = dao.save(Tag(id, "poly", "original", "name", BSONDateTime(System.currentTimeMillis()), BSONDateTime(System.currentTimeMillis()), List.empty, "doamin", List.empty))
    result.map{wr =>
      assert(wr.ok)
    }

  }

  "TagDAO" should "raad tag empty list" in {

    dao.findById(id).map {tag =>
      assert(tag.get.name == "name")
      assert(tag.get.DSPs == List.empty)
      assert(tag.get.playerIDs == List.empty)
    }

  }

  "TagDAO" should "remove tag empty list" in {
    dao.remove(id).map{wr =>
      assert(wr.ok)
    }
  }

  "TagDAO" should "save tag" in {

    val result = dao.save(Tag(id, "poly", "original", "name", BSONDateTime(System.currentTimeMillis()), BSONDateTime(System.currentTimeMillis()), List("id-1", "id-2"), "doamin", List(DSPTemplates.Nuviad, DSPTemplates.GetIntent)))
    result.map{wr =>
      assert(wr.ok)
    }

  }

  "TagDAO" should "raad tag" in {

    dao.findById(id).map {tag =>
      assert(tag.get.name == "name")
      assert(tag.get.playerIDs == List("id-1", "id-2"))
      assert(tag.get.DSPs == List(DSPTemplates.Nuviad, DSPTemplates.GetIntent))
    }

  }

  "TagDAO" should "cannot add tag (the same name)" in {
    dao.insertIfNotExists(Tag(BSONObjectID.generate(), "poly", "original", "name", BSONDateTime(System.currentTimeMillis()), BSONDateTime(System.currentTimeMillis()), List.empty, "doamin", List.empty)).map { wr =>
      assert(!wr)
    }
  }

  "TagDAO" should "add tag (dif  name)" in {
    dao.insertIfNotExists(Tag(id2, "poly", "original", "name2", BSONDateTime(System.currentTimeMillis()), BSONDateTime(System.currentTimeMillis()), List.empty, "doamin", List.empty)).map { wr =>
      assert(wr)
    }
  }

  "TagDAO" should "remove tag2" in {
    dao.remove(id2).map{wr =>
      assert(wr.ok)
    }
  }

  "TagDAO" should "remove tag" in {
    dao.remove(id).map{wr =>
      assert(wr.ok)
    }
  }

}
