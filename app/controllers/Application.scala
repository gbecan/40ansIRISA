package controllers

import java.io.File

import play.Logger
import play.api.mvc._

import scala.util.Random

object Application extends Controller {

  var videosByVP : List[(String, List[(String, String)])] = Nil

  def index = Action {
    Ok(views.html.index())
  }

  def getPlaylist() = Action { request =>

    // Choose a configuration
    val bonus = Random.nextFloat() < 0.1
    val configuration = for ((vp, videos) <- videosByVP if (!bonus && vp != "bonus") || (bonus && vp == "bonus")) yield {
        val chosenVideo = Random.shuffle(videos).head
        (vp, chosenVideo._1, chosenVideo._2)
      }


    Logger.info(configuration)

    // Create the playlist corresponding to the configuration
    val playlistContent = (for((vp, name, length) <- configuration) yield {
      List(
        "#EXT-X-DISCONTINUITY",
        "#EXTINF:" + length,
        "resources/videos/" + vp + "/" + name
      )
    }).flatten
    val playlist = List("#EXTM3U") ::: playlistContent ::: List("#EXT-X-ENDLIST")

    val playlistString = playlist.mkString("\n")

    Ok(playlistString)
  }

  def getResource(file : String) = Action {
    Ok.sendFile(new File("/var/www/resources/" + file))
  }

}