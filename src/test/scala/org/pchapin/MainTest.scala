package org.pchapin

import org.scalatest.FlatSpec

class MainTest extends FlatSpec {
  behavior of "generateStar method"

  it should "generate a star within the given radius" in {
    val radius: Double = 5
    val star: Star = Main.generateStar(radius)
    val distance: Double = Math.sqrt(
      Math.pow(star.xCoordinate, 2) +
      Math.pow(star.yCoordinate, 2) +
      Math.pow(star.zCoordinate, 2)
    )
    assert(distance <= radius)
  }

  behavior of "formatCoordinates method"

  it should "produce four fields and end with a newline" in {
    val star: Star = Main.generateStar(5)
    val formattedCoordinates: String = Main.formatCoordinates(3, star)
    val fields: Array[String] = formattedCoordinates.split(",")
    assert(4 == fields.length)
    assert(fields(3).endsWith("\n"))
  }

  it should "produce fields of the correct type" in {
    val starId: Int = 12
    val star: Star = Main.generateStar(5)
    val formattedCoordinates: String = Main.formatCoordinates(starId, star)
    val fields: Array[String] = formattedCoordinates.split(",")

    val reconstitudedStarId: Int = fields(0).toInt
    assert(reconstitudedStarId == starId)

    val xCoordinate: Double = fields(1).toDouble
    assert(star.xCoordinate - xCoordinate < 0.0001)

    val yCoordinate: Double = fields(2).toDouble
    assert(star.yCoordinate - yCoordinate < 0.0001)

    val zCoordinate: Double = fields(3).trim.toDouble
    assert(star.zCoordinate - zCoordinate < 0.0001)
  }
}
