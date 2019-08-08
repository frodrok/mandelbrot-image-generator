package com.fhdev.data

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
