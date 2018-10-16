package org.pchapin

import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.net.URI
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import scala.util.Random

object Main {
  val generator = new Random(0)

  /**
    * Parse CLI arguments and execute business logic.
    *
    * @param args CLI arguments.
    */
  def main(args: Array[String]): Unit = {
    val optionParser = new OptionParser(args)
    generateToFiles(
      optionParser.count(),
      optionParser.radius(),
      optionParser.coordinatesPath(),
      optionParser.observationsPath(),
    )
    System.exit(0)
  }

  /**
    * Respond to the case where valid non-"help" arguments were passed.
    *
    * @param count The number of stars to generate.
    * @param radius The radius of the sphere in which to place stars.
    */
  def generateToFiles(
      count: Int,
      radius: Double,
      coordinatesPath: String,
      observationsPath: String): Unit = {
    val radiansToDegrees = 360.0 / (2.0 * Math.PI)
    val earthSunBaseline = 1.496e8 / (2.998e5 * 86400.0 * 365.25)  // In light years.
    val stars: Array[Star] = Array.fill(count){ generateStar(radius) }
    logCoordinates(stars, coordinatesPath)

    // Generate the imaginary data.
    val observationsWriter: Writer = getWriter(observationsPath)
    Console.println("Generating observations for day...")
    for (dayNumber <- 1 to 365) {
      print(s"\r$dayNumber")
      for ((star, i) <- stars.iterator.zipWithIndex) {
        val formattedDayNumber = "%03d".format(dayNumber)
        val formattedStarID = "%08d".format(i)

        // Compute the nominal position.
        val eclipticPlaneDistance = Math.sqrt(
          Math.pow(star.xCoordinate, 2) + Math.pow(star.yCoordinate, 2)
        )
        val eclipticLongitude = radiansToDegrees * Math.atan2(
          star.yCoordinate,
          star.xCoordinate,
        )
        val eclipticLatitude = radiansToDegrees * Math.atan(
          star.zCoordinate / eclipticPlaneDistance
        )

        // Compute parallax deviation.
        val distance = Math.sqrt(
          Math.pow(star.xCoordinate, 2) +
          Math.pow(star.yCoordinate, 2) +
          Math.pow(star.zCoordinate, 2)
        )
        val parallaxDeviation = radiansToDegrees * Math.atan(
          earthSunBaseline / distance
        )

        // Compute the parallax shift (assumes a circular shift)
        val yearAngle = 2.0 * Math.PI * dayNumber.toDouble / 365.0  // In radians
        val adjustedLongitude =
          eclipticLongitude + parallaxDeviation * Math.cos(yearAngle)
        val adjustedLatitude =
          eclipticLatitude + parallaxDeviation * Math.sin(yearAngle)

        val formattedLongitude = "%+014.9f".format(adjustedLongitude)
        val formattedLatitude = "%+013.9f".format(adjustedLatitude)
        observationsWriter.write(
          s"$formattedDayNumber,$formattedStarID,$formattedLongitude,$formattedLatitude\n"
        )
      }
    }
    observationsWriter.close()
    Console.println("\nDone!")
  }

  def getWriter(path: String): Writer = {
    val conf: Configuration = new Configuration()
    // If HADOOP_CONF_DIR is set, this shouldn't be necessary!
    // conf.addResource(new Path("/home/hadoop/conf/core-site.xml"))
    val fs: FileSystem = FileSystem.get(new URI(path), conf)
    val stream: OutputStream = fs.create(new Path(path))
    val writer: Writer = new BufferedWriter(new OutputStreamWriter(stream))
    writer
  }

  /**
    * Log the coordinates of the given stars to a file.
    *
    * @param stars The stars whose coordinates should be logged.
    * @param outputPath The path to the output file to be created.
    */
  def logCoordinates(stars: Iterable[Star], coordinatesPath: String): Unit = {
    val coordinatesWriter: Writer = getWriter(coordinatesPath)
    for ((star, i) <- stars.iterator.zipWithIndex) {
      coordinatesWriter.write(formatCoordinates(i, star))
    }
    coordinatesWriter.close()
  }

  /**
    * Format the coordinates of the given star.
    *
    * @param starId The ID of the star whose coordinates are being formatted.
    * @param star The star whose coordinates are being formatted.
    * @return A string containing comma-separated fields and ending with a
    * newline, where the fields are star ID, x coordinate, y coordinate, and z
    * coordinate respectively.
    */
  def formatCoordinates(starId: Int, star: Star): String = {
    val formattedStarID = "%08d".format(starId)
    val coordinatePicture = "%+010.5f"
    val formattedX = coordinatePicture.format(star.xCoordinate)
    val formattedY = coordinatePicture.format(star.yCoordinate)
    val formattedZ = coordinatePicture.format(star.zCoordinate)
    s"${formattedStarID},${formattedX},${formattedY},${formattedZ}\n"
  }


  /**
    * Randomly create a star within a sphere of the given radius.
    *
    * One could also think of this method as generating points within a 3D
    * sphere.
    *
    * @param radius The radius of the sphere within which a star should be
    * placed.
    * @return A new star.
    */
  def generateStar(radius: Double): Star = {
    var xCoordinate = 0.0
    var yCoordinate = 0.0
    var zCoordinate = 0.0
    var distance = Double.MaxValue

    // Only 52.4% (pi/6) of the random points in a cube are also in the enclosed
    // sphere. This loop keeps retrying until a point in the enclosed sphere is
    // found. It should be rare for it to loop more than a few times.
    while (distance > radius) {
      xCoordinate = (2.0 * radius * generator.nextDouble()) - radius
      yCoordinate = (2.0 * radius * generator.nextDouble()) - radius
      zCoordinate = (2.0 * radius * generator.nextDouble()) - radius
      distance = Math.sqrt(
        xCoordinate * xCoordinate +
        yCoordinate * yCoordinate +
        zCoordinate * zCoordinate
      )
    }
    Star(xCoordinate, yCoordinate, zCoordinate)
  }
}
