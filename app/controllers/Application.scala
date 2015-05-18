package controllers

import java.io.File

import play.api._
import play.api.mvc._
import play.api.libs.json.Json

import scala.io.Source

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  val vps = List("vp0")


  def getPlaylist() = Action { request =>
    val length = "15"
    val videoPath = "assets/videos/vp0/bonus_canal_cut_test.ts"

    val videos = (for(vp <- vps.take(1)) yield {
      List(
        "\"#EXT-X-DISCONTINUITY",
        "#EXTINF:" + length,
        videoPath
      )
    }).flatten
    val playList = List("#EXTM3U") ::: videos ::: List("#EXT-X-ENDLIST")

    Ok(playList.mkString("\n"))
  }

  def getResource(file : String) = Action {
    Ok.sendFile(new File("resources/" + file))
  }

}