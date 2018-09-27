package com.polygon.tags.utils

import akka.http.scaladsl.model.Uri
import org.scalatest.{FlatSpec, Matchers}

class TagsUtilsSpec extends FlatSpec with Matchers {

  val  result = Uri.Query("sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}")

  "TagsUtil" should "get List of PlayerIDs" in {

    val s = "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div>\n<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div>\n<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div><"
    TagsUtils.getPlayerIDs(s) shouldBe List("XXXXXXXX", "XXXXXXXX", "XXXXXXXX")

  }

  "TagsUtil" should "get One of PlayerID" in {

    val s = "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>"
    TagsUtils.getPlayerIDs(s) shouldBe List("XXXXXXXX")

  }

  "TagsUtil" should "get List of DSP Templates" in {

    val s = "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div>\n<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div>\n<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div><"
    TagsUtils.getDSPTemplates(s) shouldBe List(result, result, result)

  }


  "TagsUtil" should "get One Template" in {

    val s = "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>"
    TagsUtils.getDSPTemplates(s) shouldBe List(result)

  }

  "TagsUtil" should "replaceQueryInTag" in {

    val s = "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>"
    TagsUtils.replaceQueryInTag(s, Uri("http://f.com?p=i&a=6").query()) shouldBe "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&a=6\"\n\ntype=\"text/javascript\"></script>"

  }

}
