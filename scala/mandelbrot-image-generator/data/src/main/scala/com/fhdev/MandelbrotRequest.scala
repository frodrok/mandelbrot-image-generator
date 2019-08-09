package com.fhdev.data

import io.circe._, io.circe.parser._
import io.circe.generic.auto._, io.circe.syntax._

package object data {

  implicit val decodeMandelbrotRequest: Decoder[MandelbrotRequest] = new Decoder[MandelbrotRequest] {
    final def apply(c: HCursor): Decoder.Result[MandelbrotRequest] =
      for {
        startX <- c.downField("start_x").as[Int]
        startY <- c.downField("start_y").as[Int]
        endX <- c.downField("end_x").as[Int]
        endY <- c.downField("end_y").as[Int]
        totalX <- c.downField("total_x").as[Int]
        totalY <- c.downField("total_y").as[Int]
        maxIter <- c.downField("max_iter").as[Int]
      } yield {
        MandelbrotRequest(startX,
          startY,
          endX,
          endY,
          totalX,
          totalY,
          maxIter)
      }
  }
}

case class MandelbrotRequest(
  startX: Int,
  startY: Int,
  endX: Int,
  endY: Int,
  totalX: Int,
  totalY: Int,
  maxIterations: Int,
)

case class MandelbrotResponse(
  data: Array[Array[Int]]
)
