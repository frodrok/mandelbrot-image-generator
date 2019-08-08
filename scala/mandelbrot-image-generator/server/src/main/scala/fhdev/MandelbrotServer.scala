package fhdev

import akka.io.{ IO, Tcp }
//import context.system // implicitly used by IO(Tcp)
import akka.actor.{ Actor, ActorRef, Props, ActorSystem }
import akka.io.{ IO, Tcp }
import akka.event.Logging
import akka.util.ByteString
import java.net.InetSocketAddress

import com.fhdev.data.MandelbrotRequest
import com.fhdev.data.MandelbrotResponse

import io.circe._, io.circe.parser._
import io.circe.generic.auto._, io.circe.syntax._
// import io.circe._
// import io.circe.parser._

import spire.implicits._
import spire.math._


import scala.annotation.tailrec

//import Tcp._
//import context.system

//import com.fhdev.MandelbrotRequest.MandelbrotReq;



object MandelbrotServer extends App {

  implicit val system = ActorSystem()
  val manager = IO(Tcp)

  // Set up our server actor
  system.actorOf(Props(new Server(1000)), "server")
}

class Server(port: Int) extends Actor {

  import Tcp._
  import context.system

  val log = Logging(context.system, this)

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", port))

  def receive = {
    case b @ Bound(localAddress) =>
      log.info(s"bound to $localAddress")
      context.parent ! b

    case CommandFailed(_: Bind) => context.stop(self)

      // When receiving a new connection, create a new
      // actor and register it as the handler
    case c @ Connected(remote, local) =>
      val handler = context.actorOf(Props[MandelbrotHandler])
      val connection = sender()
      connection ! Register(handler)
  }

}

class MandelbrotHandler extends Actor {

  import Tcp._

  val log = Logging(context.system, this)

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

  /* Receive a mandelbrotRequest and fill a mandelbrotResponse
   with the amount of calculations each pixel took */
  // Move to a separate class
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
      val h = 4.0 / totalY
      val w = 4.0 / totalX

      val x0 = -2
      val y0 = -1

      val c = Complex(x * w + x0, y * h + y0)
      mandelbrot(c, maxIterations)
    }

    def yIndexAsArray(xValue: Int, startY: Int, endY: Int, totalX: Int, totalY: Int, maxIterations: Int): Array[Int] = {
      (startY to endY).map(yValue => {
        getCalculations(xValue, yValue, totalX, totalY, maxIterations)
      }).toArray
    }

    val xAndYValues = (mbRequest.startX to mbRequest.endY).map(x => {
      yIndexAsArray(x, mbRequest.startY, mbRequest.endY, mbRequest.totalX, mbRequest.totalY, mbRequest.maxIterations)
    }).toArray

    MandelbrotResponse(xAndYValues)
  }

  def receive = {

    case Received(data) => {

      val requestJson = data.utf8String

      // Parse, get an either
      val parsed: Either[ParsingFailure, Json] = parse(requestJson)

      val extracted = parsed.fold(_ => Json.Null, x => x) // x => x = io.circe.Json

      // should be an either[Decodingfailure, MandelbrotRequest]
      val instantiated = extracted.as[MandelbrotRequest]

      // Here we extract the value and if it's good we send it to calculate()
      // which gives us a response back
      val mandelbrotResponse: MandelbrotResponse = instantiated match {
        case Right(mbR) => calculate(mbR)
        case Left(ex) => new MandelbrotResponse(Array())
      }

      val backToString: String = mandelbrotResponse.asJson.noSpaces

      sender () ! Write(ByteString(backToString))

    }
    case PeerClosed => context.stop(self)

  }
}
