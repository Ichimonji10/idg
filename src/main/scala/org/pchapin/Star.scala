package org.pchapin

/**
  * A star in space.
  *
  * The location of a star in space is assumed to be fixed and unchanging. This
  * is unrealistic, and a future version of IDG may change this design.
  *
  * @param xCoordinate The x coordinate of the star, in light years, relative to
  * the sun.
  * @param yCoordinate The y coordinate of the star, in light years, relative to
  * the sun.
  * @param zCoordinate The z coordinate of the star, in light years, relative to
  * the sun.
  */
case class Star(xCoordinate: Double, yCoordinate: Double, zCoordinate: Double)
