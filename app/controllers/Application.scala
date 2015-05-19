package controllers

import java.io.{FileFilter, FilenameFilter, File}

import play.api._
import play.api.mvc._
import play.api.libs.json.Json

import scala.io.Source
import scala.util.Random

object Application extends Controller {




  def index = Action {
    Ok(views.html.index())
  }

  def getPlaylist() = Action { request =>

    val vpDir = new File("public/videos")
    val vps = vpDir.listFiles(new FileFilter {
      override def accept(file: File): Boolean = file.isDirectory
    }).map(_.getName).toList

    // List videos by variation point
    val videosByVP = for (vp <- vps) yield {
      val dir = new File("public/videos/" + vp)

      val files = dir.listFiles(new FilenameFilter {
        override def accept(file: File, s: String): Boolean = s.endsWith(".ts")
      }).toList

      val fileNames = files.map(file => file.getName).map(name => name.substring(0, name.length - 3))
      val videos = fileNames.map(name => (name + ".ts", Source.fromFile(dir.getAbsolutePath + "/" + name + ".txt").mkString))

      (vp, videos)
    }

    // Choose a configuration
    val configuration = for ((vp, videos) <- videosByVP) yield {
      val chosenVideo = Random.shuffle(videos).head
      (vp, chosenVideo._1, chosenVideo._2)
    }

    // Create the playlist corresponding to the configuration
    val playlistContent = (for((vp, name, length) <- configuration) yield {
      List(
        "#EXT-X-DISCONTINUITY",
        "#EXTINF:" + length,
        "assets/videos/" + vp + "/" + name
      )
    }).flatten
    val playlist = List("#EXTM3U") ::: playlistContent ::: List("#EXT-X-ENDLIST")

    val playlistString = playlist.mkString("\n")
//    println(playlistString)

    Ok(playlistString)
  }

  def getResource(file : String) = Action {
    Ok.sendFile(new File("resources/" + file))
  }

}