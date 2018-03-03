package com.cleverbase.api

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Success, Failure }

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Boot extends App with Routes {

  implicit val system: ActorSystem = ActorSystem("cleverbase")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val persistenceService = new MongoPersistenceService("mongodb://localhost:27017")

  persistenceService.start.onComplete {
    case Success(_) =>
      Http().bindAndHandle(routes, "localhost", 8080)
      println(s"Server online at http://localhost:8080/")
    case Failure(f) =>
      println(f)
  }
}
