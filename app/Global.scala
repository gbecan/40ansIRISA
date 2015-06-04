import java.io.{FilenameFilter, FileFilter, File}

import controllers.Application
import play.Logger
import play.api.Application
import play.api.{Application, GlobalSettings}

import scala.io.Source

/**
 * Created by gbecan on 5/22/15.
 */
object Global extends GlobalSettings {


  override def onStart(app: Application) {
    Logger.info("starting")

    // List variation points
    val vpDir = new File("/var/www/resources/videos")

    if (vpDir.exists()) {
      val vps = vpDir.listFiles(new FileFilter {
        override def accept(file: File): Boolean =
          file.isDirectory &&
            !file.listFiles().isEmpty
      }).map(_.getName).toList.sorted

      Logger.info("variation points: " + vps)

      // List videos by variation point
      val videosByVP = for (vp <- vps) yield {
        val dir = new File("/var/www/resources/videos/" + vp)

        val files = dir.listFiles(new FilenameFilter {
          override def accept(file: File, s: String): Boolean = s.endsWith(".ts")
        }).toList

        val fileNames = files.map(file => file.getName).map(name => name.substring(0, name.length - 3))
        val videos = fileNames.map(name => (name + ".ts", Source.fromFile(dir.getAbsolutePath + "/" + name + ".txt").mkString))

        (vp, videos)

      }

      Logger.info(videosByVP.mkString("\n"))

      Application.videosByVP = videosByVP
    }

    Logger.info("done")
  }
}
