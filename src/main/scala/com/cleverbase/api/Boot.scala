package com.cleverbase.api

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Boot extends App with Routes {

  implicit val system: ActorSystem = ActorSystem("cleverbase")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def persistenceService = new MemoryPersistenceService()

  Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/")
  Await.result(system.whenTerminated, Duration.Inf)
}
