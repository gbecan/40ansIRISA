import java.io.{FilenameFilter, FileFilter, File}
import java.util.concurrent.atomic.AtomicInteger

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

    // Initialize view counter
    val initCounterFile = new File("/var/www/resources/view_counter.txt")
    if (initCounterFile.exists()) {
      val initCounter = try {
        val fileContent = Source.fromFile(initCounterFile).mkString.replaceAll("\\s", "")
        println(fileContent)
        fileContent.toInt
      } catch {
        case e : NumberFormatException => 0
      }
      Logger.info("init counter = " + initCounter)
      Application.nbOfGeneratedEpisodes = new AtomicInteger(initCounter)
    }


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
        val videos = fileNames.map(name => (name + ".ts", Source.fromFile(dir.getAbsolutePath + "/" + name + ".txt").getLines().mkString))

        (vp, videos)

      }

      Logger.info(videosByVP.mkString("\n"))

      Application.videosByVP = videosByVP
    }

    Logger.info("done")
  }
}
