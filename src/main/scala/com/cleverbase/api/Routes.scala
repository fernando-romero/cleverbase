package com.cleverbase.api

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

trait Routes {

  def persistenceService: PersistenceService

  def authenticator(credentials: Credentials): Future[Option[User]] =
    credentials match {
      case p @ Credentials.Provided(username) =>
        persistenceService.getUser(username).map {
          case Some(user) if (p.verify(user.password)) => Some(user)
          case _ => None
        }
      case _ => Future.successful(None)
    }

  lazy val routes: Route =
    authenticateBasicAsync(realm = "cleverbase", authenticator) { user =>
      complete(user.username)
    }
}
