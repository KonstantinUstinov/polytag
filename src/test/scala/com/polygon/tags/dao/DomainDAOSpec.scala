package com.polygon.tags.dao

import com.polygon.tags.utils.ConfigProvider
import org.scalatest.AsyncFlatSpec
import reactivemongo.bson.BSONObjectID

class DomainDAOSpec extends AsyncFlatSpec with ConfigProvider with DomainDAO {

  val id = BSONObjectID.generate()

  "DomainDAO" should "save domain" in {
    val result = domain_dao.save(Domain(id, "original"))
    result.map{wr =>
      assert(wr.ok)
    }
  }

  "DomainDAO" should "read domains" in {
    domain_dao.getAll.map { domains =>
      assert(domains.length == 1)
      assert(domains.head.path == "original")
    }
  }

  "DomainDAO" should "update domain" in {
    domain_dao.update(Domain(id, "original2")) map { wr =>
      assert(wr.ok)
    }
  }

  "DomainDAO" should "read domain" in {
    domain_dao.getAll.map { domains =>
      assert(domains.length == 1)
      assert(domains.head.path == "original2")
    }
  }

/*
  "DomainDAO" should "remove user" in {
    domain_dao.remove(id).map{wr =>
      assert(wr.ok)
    }
  } */

}
