package controllers

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

import play.Logger
import play.api.libs.json.{JsString, JsArray, JsObject}
import play.api.mvc._

import scala.util.Random

object Application extends Controller {

  var videosByVP : List[(String, List[(String, String)])] = Nil

  var nbOfGeneratedEpisodes : AtomicInteger = new AtomicInteger(0)

  def index = Action {
    Ok(views.html.index(nbOfGeneratedEpisodes.get()))
  }

  def generatePlaylist() = Action { request =>
    // Choose a configuration
    val bonus = Random.nextFloat() < 0.01
    val configuration = for (((vp, videos), vpIndex) <- videosByVP.zipWithIndex
                             if (!bonus && vp != "bonus") || (bonus && vp == "bonus")) yield {
      val videoIndex = Random.nextInt(videos.size)
      (vpIndex, videoIndex)
    }

    val currentCounter = nbOfGeneratedEpisodes.incrementAndGet()
    Logger.info(currentCounter.toString)

    val playlistURL = "get-playlist?" + configuration.map(vp => "vp" + vp._1 + "=" + vp._2).mkString("&")

    val jsonConfiguration = JsObject(Seq(
      "playlistURL" -> JsString(playlistURL),
      "counter" -> JsString(currentCounter.toString)
    ))

    Ok(jsonConfiguration)
  }

  def safeIntParsing(s : String) : Int = {
    try {
      s.toInt
    } catch {
      case e : NumberFormatException => 0
    }
  }

  def getPlaylist() = Action { request =>
    // Get the configuration
    val parameters = request.queryString

    val configuration = (for (((vp, videos), vpIndex) <- videosByVP.zipWithIndex) yield {
      val videoIndexParameter = parameters.get("vp" + vpIndex)
      if (videoIndexParameter.isDefined) {

        val videoIndex = videoIndexParameter.get.head
        val chosenVideo = videos.lift(safeIntParsing(videoIndex))

        if (chosenVideo.isDefined) {
          Some((vp, chosenVideo.get._1, chosenVideo.get._2))
        } else {
          None // Wrong parameter
        }

      } else {
        None
      }
    }).flatten

    Logger.info(configuration.map(_._2).mkString(", "))

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