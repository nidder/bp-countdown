package org.nidsProjects

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.dsl.io._
import org.http4s.implicits._
import com.comcast.ip4s._

import java.time.{Duration, LocalDateTime}
import scala.sys.env
import scala.util.Try

object blackPinkApp extends IOApp {

  val concertDate: LocalDateTime = LocalDateTime.of(2025, 8, 15, 17, 0)

  def countdown(now: LocalDateTime, endDate: LocalDateTime): String = {
    val duration = Duration.between(now, endDate)
    val days = duration.toDays
    val hours = duration.minusDays(days).toHours
    val minutes = duration.minusDays(days).minusHours(hours).toMinutes
    val seconds = duration.minusDays(days).minusHours(hours).minusMinutes(minutes).getSeconds

    s"Ellie & Nida's BlackPink Concert Countdown is.....Days: $days, Hours: $hours, Minutes: $minutes, Seconds: $seconds"
  }
  def run(args: List[String]): IO[ExitCode] = {
    println("Server is starting...")
    val port: Port = env.get("PORT")
      .flatMap(p => Try(p.toInt).toOption)
      .flatMap(Port.fromInt)
      .getOrElse(port"8080")
    val host: Host = host"0.0.0.0"

  val service = HttpRoutes.of[IO] {
    case GET -> Root =>
      val now = LocalDateTime.now()
      Ok(countdown(now, concertDate))
  }

  val httpApp = service.orNotFound

  EmberServerBuilder.default[IO]
    .withHost(host)
    .withPort(port)
    .withHttpApp(httpApp)
    .build
    .use(_ => IO.never)
    .as(ExitCode.Success)

  }
}
