package com.cleverbase.api

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Success, Failure }

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

trait Routes extends JsonSupport {

  def persistenceService: PersistenceService

  def authenticator(credentials: Credentials): Future[Option[User]] =
    credentials match {
      case p @ Credentials.Provided(username) =>
        persistenceService.getUser(username).map {
          case Some(user) if (p.verify(user.password)) =>
            Some(user)
          case _ =>
            None
        }
      case _ =>
        Future.successful(None)
    }

  lazy val routes: Route =
    concat(
      post {
        path("users") {
          entity(as[CreateUser]) { data =>
            onComplete(persistenceService.createUser(data)) {
              case Success(user) =>
                complete(StatusCodes.Created, user.toShow)
              case Failure(f) =>
                failWith(f)
            }
          }
        }
      },
      delete {
        concat(
          path("users") {
            onComplete(persistenceService.deleteUsers()) {
              case Success(_) =>
                complete(StatusCodes.NoContent)
              case Failure(f) =>
                failWith(f)
            }
          },
          path("secrets") {
            onComplete(persistenceService.deleteSecrets()) {
              case Success(_) =>
                complete(StatusCodes.NoContent)
              case Failure(f) =>
                failWith(f)
            }
          }
        )
      },
      authenticateBasicAsync(realm = "cleverbase", authenticator) { user =>
        concat(
          (path("users") & get) {
            onComplete(persistenceService.getUsers()) {
              case Success(users) =>
                complete(StatusCodes.OK, ShowUsers(users.map(_.toShow)))
              case Failure(f) =>
                failWith(f)
            }
          },
          (path("login") & post) {
            if (user.isSuper) {
              onComplete(persistenceService.login(user.username)) {
                case Success(_) =>
                  complete(StatusCodes.NoContent)
                case Failure(f) =>
                  failWith(f)
              }
            } else {
              complete(StatusCodes.Forbidden)
            }
          },
          path("secrets") {
            if (user.isLoggedIn) {
              concat(
                post {
                  entity(as[CreateSecret]) { data =>
                    onComplete(persistenceService.createSecret(user.username, data)) {
                      case Success(secret) =>
                        complete(StatusCodes.Created, secret.toSelf)
                      case Failure(f) =>
                        failWith(f)
                    }
                  }
                },
                get {
                  onComplete(persistenceService.getSecretsByOwner(user.username)) {
                    case Success(secrets) =>
                      complete(StatusCodes.Created, SelfSecrets(secrets.map(_.toSelf)))
                    case Failure(f) =>
                      failWith(f)
                  }
                },
                put {
                  entity(as[UpdateSecret]) { data =>
                    onComplete(persistenceService.updateSecret(user.username, data)) {
                      case Success(Some(secret)) =>
                        complete(StatusCodes.Created, secret.toSelf)
                      case Success(None) =>
                        complete(StatusCodes.NotFound)
                      case Failure(f) =>
                        failWith(f)
                    }
                  }
                }
              )
            } else {
              complete(StatusCodes.Forbidden)
            }
          },
          path("shared") {
            get {
              onComplete(persistenceService.getSecretsSharedWith(user.username)) {
                case Success(secrets) =>
                  complete(StatusCodes.Created, SharedSecrets(secrets.map(_.toShared)))
                case Failure(f) =>
                  failWith(f)
              }
            }
          }
        )
      }
    )
}
