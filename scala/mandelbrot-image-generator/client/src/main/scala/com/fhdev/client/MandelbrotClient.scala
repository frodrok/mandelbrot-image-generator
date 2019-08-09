package com.fhdev.client

import com.fhdev.data.MandelbrotRequest
import com.fhdev.data.MandelbrotResponse

import java.net.InetSocketAddress
import scala.util.Success
import scala.util.Failure
import scala.concurrent.Future
import scala.language.postfixOps

import io.circe._, io.circe.parser._
import io.circe.generic.auto._, io.circe.syntax._

import scala.concurrent.duration.DurationInt

import scalaz._
import scalaz.concurrent.Task

import java.nio.charset.StandardCharsets;

import java.util.concurrent.{Executors, ExecutorService}

import java.io._
import java.net.{ Socket }

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext

import scala.collection.mutable.HashMap

import java.awt.Point
import java.awt.Color;
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


import scala.collection.mutable.ListBuffer

import java.nio.charset.Charset;
import java.nio.ByteBuffer

final case class NotEnoughArgsException(message: String) extends Exception

object MandelbrotClient extends App {


  if (args.length < 5) throw new NotEnoughArgsException("Please provide partsAmount, imageWidth, imageHeight, maxIterations and outputFilename as arguments")

  val partsAmount = args(0).toInt
  val imageWidth = args(1).toInt
  val imageHeight = args(2).toInt
  val maxIterations = args(3).toInt
  val outputFilename = args(4)

  val hosts = List("127.0.0.1:1000",
    "127.0.0.1:1001",
    "127.0.0.1:1002",
    "127.0.0.1:1003"
  )

  println("Starting client!")

  def sendAndReceive(bytes: Array[Byte], host: String): String = {

    val hostname = host.split(":")(0)
    val port = host.split(":")(1).toInt

    val sock = new Socket(hostname, port)

    // Send our message bytes
    sock.getOutputStream.write(bytes)

    val in = sock.getInputStream
    val messageLengthBytes = (0 until 4).map(_.toByte).toArray
    val readBytes = in.read(messageLengthBytes, 0, 4)

    val mLength = ByteBuffer.wrap(messageLengthBytes).getInt

    var totalReadBytes = 0
    val messageBytes = new Array[Byte](mLength)

    var start = 0

    // Read until we have all our expected bytes or stop if the
    // server doesn't send anything
    while (totalReadBytes < mLength) {

      val tempBytes = new Array[Byte](60000)
      val serverSentAmountBytes = in.read(tempBytes, 0, 60000)

      if (serverSentAmountBytes > 0) {

        for (index <- 0 to (serverSentAmountBytes - 1)) {
          messageBytes(index + start) = tempBytes(index)
        }

        totalReadBytes += serverSentAmountBytes
        start += serverSentAmountBytes

      } else {
        // To stop reading
        totalReadBytes = mLength + 1
      }
    }

    return new String(messageBytes, StandardCharsets.UTF_8)
  }

  def splitAndCalculate(partsAmount: Int, width: Int, height: Int, maxIterations: Int, hosts: List[String]): List[Tuple2[Point, MandelbrotResponse]] = {


    val onePartX = width / partsAmount
    val onePartY = height / partsAmount

    val tasksToWaitFor = ListBuffer[Task[Tuple2[Point, MandelbrotResponse]]]()

    var generatedParts = 0

    // For each part to generate, create a scalaz.Task and add it to
    // tasksTowaitfor
    for (xPart <- 0 until partsAmount) {
      for (yPart <- 0 until partsAmount) {

        val startX = xPart * onePartX
        val startY = yPart * onePartY

        val endX = (xPart + 1) * onePartX
        val endY = (yPart + 1) * onePartY

        def fireAndForget(part: Int): Task[Tuple2[Point, MandelbrotResponse]] = Task {

          val mbRequest = MandelbrotRequest(startX,
            startY,
            endX,
            endY,
            width,
            height,
            maxIterations)

          val host = hosts(part)

          // Serialize our request to json, send it to the server
          // and deserialize the response 
          val requestAsString = mbRequest.asJson.noSpaces + "\n"

          val result = sendAndReceive(requestAsString.getBytes("UTF8"), host)

          val parsed = parse(result)
          val extracted = parsed.fold(_ => Json.Null, x => x)
          val instantiated = extracted.as[MandelbrotResponse]

          val mbResponse: MandelbrotResponse = instantiated match {
            case Right(mbResponse) => mbResponse
            case Left(ex) => {
              println(s"json error in part $generatedParts")
              new MandelbrotResponse(Array())
            }
          }

          val point = new Point(mbRequest.startX, mbRequest.startY)

          (point, mbResponse)
        }

        val task = fireAndForget(generatedParts)

        tasksToWaitFor += task
        generatedParts += 1
      }
    }

    Task.gatherUnordered(tasksToWaitFor.toSeq).run
  }

  val bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
  println(s"Creating image buffer of [$imageWidth, $imageHeight] pixels")

  println("Fetching data...")
  val data = splitAndCalculate(partsAmount, imageWidth, imageHeight, maxIterations, hosts)

  println("Data fetching done, drawing image...")

  // Loop the data received and transform calculations to color
  // and draw it to the image buffer
  // x will be of Tuple2[Point, MandelbrotResponse] type
  data foreach { x => {

    val offsetPoint = x._1
    val mbResponse = x._2

    val rawData: Array[Array[Int]] = mbResponse.data

    for (xIndex <- 0 until rawData.length) {

      val yValues = rawData(xIndex)

      for (yIndex <- 0 until yValues.length) {

        val calculations = yValues(yIndex)
        val color = Color.HSBtoRGB(calculations / 256f,
          1,
          calculations / (calculations + 8f))

        bi.setRGB(xIndex + offsetPoint.x, yIndex + offsetPoint.y, color)
      }
    }
  }
  }

  // Save the image buffer to file
  ImageIO.write(bi, "PNG", new File(outputFilename))

}

