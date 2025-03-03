package org.nidsProjects

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.dsl.io._
import org.http4s.implicits._
import com.comcast.ip4s._
import java.time.{Duration, LocalDateTime}

object blackPinkApp extends IOApp {

  val concertDate: LocalDateTime = LocalDateTime.of(2025, 8, 15, 17, 0)

  def countdown(now: LocalDateTime, endDate: LocalDateTime): String = {
    val duration = Duration.between(now, endDate)
    val days = duration.toDays
    val hours = duration.minusDays(days).toHours
    val minutes = duration.minusDays(days).minusHours(hours).toMinutes
    val seconds = duration.minusDays(days).minusHours(hours).minusMinutes(minutes).getSeconds

    s"Days: $days, Hours: $hours, Minutes: $minutes, Seconds: $seconds"
  }

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "countdown" =>
      val now = LocalDateTime.now()
      Ok(countdown(now, concertDate))
  }

  val httpApp = service.orNotFound

  val server = EmberServerBuilder.default[IO]
    .withHost(ipv4"127.0.0.1")
    .withPort(port"8080")
    .withHttpApp(httpApp)
    .build

  def run(args: List[String]): IO[ExitCode] = {
    println("Server is starting...")
    server.use(_ => IO.never).as(ExitCode.Success).onCancel {
      IO(println("Server has been cancelled!"))
    }
  }
}
