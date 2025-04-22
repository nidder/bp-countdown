package org.nidsProjects

import cats.effect.{ExitCode, IO, IOApp}
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.staticcontent._
import org.http4s.server.Router
import com.comcast.ip4s._

import java.time.{Duration, LocalDateTime}
import scala.sys.env
import scala.util.Try

object blackPinkApp extends IOApp {

  val concertDate: LocalDateTime = LocalDateTime.of(2025, 8, 15, 17, 0)

  // Case class for JSON formatting
  case class Countdown(days: Long, hours: Long, minutes: Long, seconds: Long)

  // Function to calculate countdown
  def getCountdown: Countdown = {
    val now = LocalDateTime.now()
    val duration = Duration.between(now, concertDate)

    Countdown(
      days = duration.toDays,
      hours = duration.toHoursPart,
      minutes = duration.toMinutesPart,
      seconds = duration.toSecondsPart
    )
  }

  def run(args: List[String]): IO[ExitCode] = {
    println("Server is starting...")

    val port: Port = env.get("PORT")
      .flatMap(p => Try(p.toInt).toOption)
      .flatMap(Port.fromInt)
      .getOrElse(port"8080")

    val host: Host = host"0.0.0.0"

    // Static content routing (serve images, CSS, JS)
    val staticFileService = HttpRoutes.of[IO] {
      case req @ GET -> Root / "static" / "images" / file =>
        StaticFile.fromResource(s"/static/images/$file", Some(req)).getOrElseF(NotFound()) // Serve images correctly
      case req @ GET -> Root / "index.html" =>
        StaticFile.fromResource("/index.html", Some(req)).getOrElseF(NotFound()) // Serve index.html
    }

    // Service routing for countdown (JSON)
    val service = HttpRoutes.of[IO] {
      case GET -> Root =>
        StaticFile.fromResource("/index.html", Some(Request[IO]())).getOrElseF(NotFound()) // Serve index.html on root
      case GET -> Root / "countdown" =>
        Ok(getCountdown.asJson)
    }

    // Combine static files routing with the app service
    val httpApp = Router(
      "/" -> service, // Serve the HTML on root
      "/static" -> staticFileService // Static files like images
    ).orNotFound

    // Start the server
    EmberServerBuilder.default[IO]
      .withHost(host)
      .withPort(port)
      .withHttpApp(httpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
