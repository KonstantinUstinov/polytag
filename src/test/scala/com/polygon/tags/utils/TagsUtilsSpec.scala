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

  "TagsUtil" should "not return Exeption" in {

    val s = "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid=[APP_BUNDLE]&cb=[CB]&appn=[APP_NAME]&appv=[APP_VER]&appb=[APP_BUNDLE]&appsu=[APP_STORE_URL]&appidfa=[APP_IDFA]&appaid=[APP_AID]&appsi=[APP_STORE_ID]&appc=[APP_CATEGORY]&country=[COUNTRY_ID]&loc=[LOCATION]&loclong=[LOCATION_LONG]&loclat=[LOCATION_LAT]&deviceid=[DEVICEID]&dnt=[DNT]&w=300&h=250&d=[APP_BUNDLE]\"\n\ntype=\"text/javascript\"></script>"
    TagsUtils.replaceQueryInTag(s, Uri("http://f.com?p=5b9c2db31e00001e00939fa1&sid=$%7BAPP_BUNDLE_ID%7D&cb=$%7BCACHE_BUSTER%7D&appn=$%7BAPP_NAME%7D&appb=$%7BAPP_BUNDLE_ID%7D&appsu=$%7BAPP_STORE_URL%7D&appidfa=$%7BIOS_IFA%7D&appaid=$%7BGOOGLE_AID%7D&appsi=$%7BAPP_BUNDLE_ID%7D&loclong=$%7BLONGITUDE%7D&loclat=$%7BLATITUDE%7D&w=480&h=320&ho=1&d=$%7BAPP_BUNDLE_ID%7D&c4=$%7BIMPRESSION_CONTEXT%7D&c5=$%7BIMPRESSION_CONTEXT%7D").query()) shouldBe
      "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid=$%7BAPP_BUNDLE_ID%7D&cb=$%7BCACHE_BUSTER%7D&appn=$%7BAPP_NAME%7D&appb=$%7BAPP_BUNDLE_ID%7D&appsu=$%7BAPP_STORE_URL%7D&appidfa=$%7BIOS_IFA%7D&appaid=$%7BGOOGLE_AID%7D&appsi=$%7BAPP_BUNDLE_ID%7D&loclong=$%7BLONGITUDE%7D&loclat=$%7BLATITUDE%7D&w=480&h=320&ho=1&d=$%7BAPP_BUNDLE_ID%7D&c4=$%7BIMPRESSION_CONTEXT%7D&c5=$%7BIMPRESSION_CONTEXT%7D\"\n\ntype=\"text/javascript\"></script>"

  }

  "TagsUtil" should "replaceQueryInTag by List" in {

    val s = "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div>\n<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div>\n<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div>"
    TagsUtils.replaceQueryInTag(s, Uri("http://f.com?p=5b9c2db31e00001e00939fa1&sid=$%7BAPP_BUNDLE_ID%7D&cb=$%7BCACHE_BUSTER%7D&appn=$%7BAPP_NAME%7D").query()) shouldBe
      "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid=$%7BAPP_BUNDLE_ID%7D&cb=$%7BCACHE_BUSTER%7D&appn=$%7BAPP_NAME%7D\"\n\ntype=\"text/javascript\"></script>\n\n</div>\n<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid=$%7BAPP_BUNDLE_ID%7D&cb=$%7BCACHE_BUSTER%7D&appn=$%7BAPP_NAME%7D\"\n\ntype=\"text/javascript\"></script>\n\n</div>\n<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid=$%7BAPP_BUNDLE_ID%7D&cb=$%7BCACHE_BUSTER%7D&appn=$%7BAPP_NAME%7D\"\n\ntype=\"text/javascript\"></script>\n\n</div>"

  }

  "TagsUtil" should "find all style params in div" in {

    val s = "<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div>\n<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 301px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div>\n<div id=\"videoXXXXXXX{{TIMESTAMP}}\" style=\"width: 300px; height: 250px;\">\n\n<script src=\"http://p.videoalg.com/player/player.js?p=XXXXXXXX&sid={{APP_BUNDLE}}_{{USER_COUNTRY}}_{{EXCHANGE}}_{{CAMPAIGN_ID}}&cb={{TIMESTAMP}}&c1={{CLICK_ID}}&appn={{APP_NAME_ENC}}&appv={{APP_VERSION}}&appb={{APP_BUNDLE}}&appsu={{APP_STOREURL}}&appidfa={{DEVICE_IFA}}&appaid={{DEVICE_IFA}}&appsi={{APP_BUNDLE}}&appc={{APP_CATEGORY_ENC}}&country={{USER_COUNTRY}}&loc={{USER_GEO_LAT}}&loclong={{USER_GEO_LNG}}&loclat={{USER_GEO_LAT}}&deviceid={{DEVICE_IFA}}&w=480&h=320&d={{APP_BUNDLE}}\"\n\ntype=\"text/javascript\"></script>\n\n</div>"
    TagsUtils.getSizeOfAd(s) shouldBe List((300, 250), (301, 250), (300, 250))
  }

  "TagsUtil" should "conver style to Json" in {
    TagsUtils.convertStyleToSize("width: 300px; height: 250px;") shouldBe Some((300, 250))
    TagsUtils.convertStyleToSize("width: 300; height: 250;") shouldBe Some((300, 250))
    TagsUtils.convertStyleToSize("height: 250; width: 300; ff: 3;") shouldBe Some((300, 250))
    TagsUtils.convertStyleToSize("ff: 3;") shouldBe None
  }
}
