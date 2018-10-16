package org.pchapin

import org.rogach.scallop.ScallopConf

/** A CLI option parser. */
class OptionParser(arguments: Seq[String]) extends ScallopConf(arguments) {
  val coordinatesPath = opt[String](
    default = Some("coordinates.csv"),
    descr = "The file into which star coordinates should be written.",
    noshort = true,
  )
  val count = opt[Int](
    default = Some(10),
    descr = "The number of stars to generate.",
  )
  val observationsPath = opt[String](
    default = Some("observations.csv"),
    descr = "The file into which star observations should be written.",
    noshort = true,
  )
  val radius = opt[Double](
    default = Some(1000),
    descr = "The radius of the sphere within which stars are placed.",
  )
  verify()
}
