package controllers

import java.io.File

import play.api._
import play.api.mvc._
import play.api.libs.json.Json

import scala.io.Source

object Application extends Controller {

  val vpName = Map(
    "vp1" -> "Souvenir n°1",
    "vp2" -> "Bref.",
    "vp3" -> "J'étais en train de mater une vidéo de Canal...",
    "vp4" -> "Réplique culte n°1",
    "vp5" -> "Réplique culte n°2",
    "vp6" -> "Réplique culte n°3",
    "vp7" -> "Souvenir n°2",
    "vp8" -> "Tu m'écoutes?!",
    "vp9" -> "Jean Jacques",
    "vp10" -> "Oh non! n°1",
    "vp11" -> "Oh non! n°2",
    "vp12" -> "Oh non! n°3",
    "vp13" -> "J'ai découvert ce qu'était un vrai commentateur...",
    "vp14" -> "J'ai surpris mon père devant la télé...",
    "vp15" -> "Bref Canal a 30 ans",
    "vp16" -> "Souvenir n°3",
    "vp17" -> ":D",
    "vp18" -> "Algorithme super puissant"
  )

  def index = Action {
    Ok(views.html.index())
  }



  def getPlaylist() = Action { request =>
    val parameters = request.queryString

    val sortedParameters = parameters.toList.sortBy(t => t._1.substring(2).toInt)
    Logger.info("Request(" + sortedParameters.map(p => p._2(0)).mkString(",") + ")")



    val length = "15"
    val videoPath = "assets/videos/vp0/output.ts"

    val playList = List(
      "#EXTM3U",
      "#EXTINF:" + length,
      videoPath).mkString("\n")



    Ok(playList)
  }

  def getPlaylistHTML5() = Action { request =>
    val parameters = request.queryString

    val sortedParameters = parameters.toList.sortBy(t => t._1.substring(2).toInt)
    Logger.info("Request(" + sortedParameters.map(p => p._2(0)).mkString(",") + ")")

    val playList = for ((vp, option) <- sortedParameters) yield {
      "http://bref30cdn.wildmoka.com/vidv2/" + option(0) + "_med.webm"
    }


    Ok(Json.toJson(playList))
  }


  def getVPs() = Action {
    val dir = new File("resources/options-by-vp")
    val vpFiles = dir.listFiles().sortBy(_.getName.substring(2).toInt)
    val vps = for (vpFile <- vpFiles) yield {
      val options = for (optionFile <- vpFile.listFiles()) yield {
        var name = optionFile.getName
        name = name.substring(0, name.length - 4)
        val poster = "resources/poster-frames/" + name + "_med0.png"
        (name, poster)
      }
      (vpName(vpFile.getName), options.toList)
    }

    val json = Json.toJson(
      vps.map(vp => Json.toJson(Map(
        "name" -> Json.toJson(vp._1),
        "options" -> Json.toJson(
          vp._2.map(option =>
            Json.toJson(Map(
              "id" -> Json.toJson(Json.toJson(option._1)),
              "poster" -> Json.toJson(Json.toJson(option._2))
            ))
          )),
        "selected" -> Json.toJson(vp._2(0)._1),
        "visible" -> Json.toJson(true)
      )))
    )

    Ok(json)
  }

  def getResource(file : String) = Action {
    Ok.sendFile(new File("resources/" + file))
  }

}