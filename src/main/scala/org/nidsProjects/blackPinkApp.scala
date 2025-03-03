package org.nidsProjects

import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec;
import org.http4s.dsl.io


object blackPinkApp extends App {

  val concertDate = LocalDateTime.of(2025, 0x8, 15, 17, 0)
  val now = LocalDateTime.now()

  @tailrec
  def countdown(today: LocalDateTime, endDate: LocalDateTime): Unit = {

    val now = LocalDateTime.now()
    val duration = Duration.between(now, endDate)
    val seconds = duration.getSeconds
    val minutes = duration.toMinutes
    val hours = duration.toHours
    val days = duration.toDaysPart

    println(s"Seconds: $seconds, Minutes: $minutes, Hours: $hours, Days: $days")
    Thread.sleep(1000)

    countdown(today, endDate)
  }
  countdown(now, concertDate)
}