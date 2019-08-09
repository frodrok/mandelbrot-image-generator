package fhdev

import java.net.InetSocketAddress

import com.fhdev.data.MandelbrotRequest
import com.fhdev.data.MandelbrotResponse

import io.circe._, io.circe.parser._
import io.circe.generic.auto._, io.circe.syntax._

import spire.implicits._
import spire.math._

import java.io._
import java.nio.ByteBuffer

import java.util.Arrays

import java.net.{ ServerSocket, Socket }

import scala.annotation.tailrec

object MandelbrotServer extends App {

  val port = args(0).toInt

  new Server(port).start  
}

class Server(port: Int){

  def start = {

    val listeningSocket = new ServerSocket(port)

    @tailrec def handlerLoop(running: Boolean): Boolean = {

      if (!running) running

      // This blocks until someone has connected on $port
      val connection = listeningSocket.accept

      val input = new BufferedReader(new InputStreamReader(connection.getInputStream))
      val outStream = connection.getOutputStream

      // This blocks until someone has sent $x + '\n' on $port
      val readLine = input.readLine()

      // When we receive a request, send it to MandelbrotHandler
      // and get a response back
      val response = MandelbrotHandler.getMandelbrotResponse(readLine)
      val length = response.length
      val responseAsBytes = response.getBytes("UTF-8")

      val lengthAsBytes = ByteBuffer.allocate(4).putInt(length).array()

      // Calculate how many 60k packets we need from the length / 60000
      // and the remainder for the last small packet
      // for example if length = 830000 bytes, 830000 / 60000 = 13
      // so we need 13 full size packets and then 830000 % 60000 = size of
      // the last packet (50000)
      val fullSizePackets: Int = if (length > 60000) (length / 60000) else 1
      val lastPacketSize: Int = if (fullSizePackets > 1) length % 60000 else 0

      // Send 4 bytes containing the total length of the message
      // and then all of the bytes split into packets of 60k bytes

      // Copy bytes into 60k packets with 1 smaller packet at the end
      def generatePackets(fullSizePackets: Int, lastPacketSize: Int, bytes: Array[Byte]): List[Array[Byte]] = {

        val populatedPackets: List[Array[Byte]] = List.range(0, fullSizePackets) map (x => {
          Arrays.copyOfRange(bytes, x * 60000, (x+1) * 60000)
        })

        val end = (fullSizePackets * 60000)
        val lastPacket: Array[Byte] = Arrays.copyOfRange(bytes, end, end+lastPacketSize)

        populatedPackets ::: List(lastPacket)
      }

      val generatedPackets = generatePackets(fullSizePackets, lastPacketSize, responseAsBytes)

      // Join with length packet as head
      val bytesToWrite = List(lengthAsBytes) ::: generatedPackets

      // Hit send button
      bytesToWrite foreach(outStream.write(_))

      // Recurse and accept a new connection
      handlerLoop(true)
    }

    handlerLoop(true)

  }

}

object MandelbrotHandler {

  def getMandelbrotResponse(line: String): String = {

    // Parse line into a MandelbrotRequest, do calculations and return
    // a MandelbrotResponse as a string

    val parsed = parse(line)

    val extracted = parsed.fold(_ => Json.Null, x => x)

    val instantiated = extracted.as[MandelbrotRequest]

    val stringResponse = instantiated match {
        case Right(mbR) => calculate(mbR).asJson.noSpaces
        case Left(ex) => "could not parse json"
    }

    return stringResponse
  }

  // Receive a mandelbrotRequest and fill a mandelbrotResponse
  // with the amount of calculations each pixel took
  def calculate(mbRequest: MandelbrotRequest): MandelbrotResponse = {

    def mandelbrot(c: Complex[Double], maxIterations: Int): Int = {
      @tailrec def loop(z: Complex[Double], n: Int): Int = {
        if (n >= maxIterations) n
        else if (z.abs > 2.0) n - 1
        else loop(z * z + c, n + 1)

      }
      loop(c, 1)
    }

    def getCalculations(x: Int, y: Int, totalX: Int, totalY: Int, maxIterations: Int): Int = {
      val h = 2.0 / totalY
      val w = 2.0 / totalX

      val x0 = -2
      val y0 = -1

      val c = Complex(x * w + x0, y * h + y0)

      mandelbrot(c, maxIterations)
    }

    def yIndexAsArray(xValue: Int, startY: Int, endY: Int, totalX: Int, totalY: Int, maxIterations: Int): Array[Int] = {
      (startY until endY).map(yValue => {
        getCalculations(xValue, yValue, totalX, totalY, maxIterations)
      }).toArray
    }

    // Generate a two dimensional array with X and Y values
    // with variables from the request
    val xAndYValues = (mbRequest.startX until mbRequest.endX).map(x => {
       yIndexAsArray(x, mbRequest.startY, mbRequest.endY, mbRequest.totalX, mbRequest.totalY, mbRequest.maxIterations)
    }).toArray

    MandelbrotResponse(xAndYValues)
  }

}
