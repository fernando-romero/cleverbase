package com.cleverbase.api

import scala.concurrent.Future

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.mockito.Mockito.reset

class RoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
    with Routes with MockitoSugar with JsonSupport with BeforeAndAfterEach {

  override val persistenceService = mock[PersistenceService]

  override def afterEach() {
    reset(persistenceService)
  }

  "POST /users" should {
    "create an user" in {
      val data = CreateUser("user1", "pass1", "user2")
      val user = data.toUser
      val showUser = user.toShow
      when(persistenceService.createUser(data)).thenReturn(Future.successful(user))

      Post("/users", data) ~> routes ~> check {
        status shouldBe StatusCodes.Created
        entityAs[ShowUser] shouldBe showUser
        verify(persistenceService).createUser(data)
      }
    }
  }

  "DELETE /users" should {
    "delete users" in {
      when(persistenceService.deleteUsers()).thenReturn(Future.successful(()))

      Delete("/users") ~> routes ~> check {
        status shouldBe StatusCodes.NoContent
        verify(persistenceService).deleteUsers()
      }
    }
  }

  "DELETE /secrets" should {
    "delete secrets" in {
      when(persistenceService.deleteSecrets()).thenReturn(Future.successful(()))

      Delete("/secrets") ~> routes ~> check {
        status shouldBe StatusCodes.NoContent
        verify(persistenceService).deleteSecrets()
      }
    }
  }

  "GET /users" should {
    "retrieve users" in {
      val user = User("user1", "pass1", "user2")
      when(persistenceService.getUser(user.username)).thenReturn(Future.successful(Some(user)))
      when(persistenceService.getUsers()).thenReturn(Future.successful(user :: Nil))

      val creds = BasicHttpCredentials("user1", "pass1")
      Get("/users") ~> addCredentials(creds) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        entityAs[ShowUsers] shouldBe ShowUsers(user.toShow :: Nil)
        verify(persistenceService).getUser(user.username)
        verify(persistenceService).getUsers()
      }
    }
  }

  "POST /login" should {
    "login an user" in {
      val user = User("user1", "pass1", "user2")
      val secret = "secret1"
      when(persistenceService.getUser(user.username)).thenReturn(Future.successful(Some(user)))
      when(persistenceService.login(user, Some(secret))).thenReturn(Future.successful(true))

      val creds = BasicHttpCredentials("user1", "pass1")
      val header = RawHeader("Secret", secret)
      Post("/login") ~> addCredentials(creds) ~> addHeader(header) ~> routes ~> check {
        status shouldBe StatusCodes.NoContent
        verify(persistenceService).getUser(user.username)
        verify(persistenceService).login(user, Some(secret))
      }
    }
  }

  "POST /secrets" should {
    "create a secret" in {
      val user = User("user1", "pass1", "user2", false, true)
      val data = CreateSecret("secret1", "secret text 1")
      val secret = data.toSecret(user.username)
      val selfSecret = secret.toSelf
      when(persistenceService.getUser(user.username)).thenReturn(Future.successful(Some(user)))
      when(persistenceService.createSecret(user.username, data))
        .thenReturn(Future.successful(secret))

      val creds = BasicHttpCredentials("user1", "pass1")
      Post("/secrets", data) ~> addCredentials(creds) ~> routes ~> check {
        status shouldBe StatusCodes.Created
        entityAs[SelfSecret] shouldBe selfSecret
        verify(persistenceService).getUser(user.username)
        verify(persistenceService).createSecret(user.username, data)
      }
    }
  }

  "GET /secrets" should {
    "retrieve own secrets" in {
      val user = User("user1", "pass1", "user2", false, true)
      val secret = Secret("secret1", "secret text 1", "user1")
      when(persistenceService.getUser(user.username)).thenReturn(Future.successful(Some(user)))
      when(persistenceService.getSecretsByOwner(user.username))
        .thenReturn(Future.successful(secret :: Nil))

      val creds = BasicHttpCredentials("user1", "pass1")
      Get("/secrets") ~> addCredentials(creds) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        entityAs[SelfSecrets] shouldBe SelfSecrets(secret.toSelf :: Nil)
        verify(persistenceService).getUser(user.username)
        verify(persistenceService).getSecretsByOwner(user.username)
      }
    }
  }

  "PUT /secrets" should {
    "share a secret" in {
      val user = User("user1", "pass1", "user2", false, true)
      val data = UpdateSecret("secret1", "user1" :: Nil)
      val secret = Secret(data.id, "secret text 1", user.username, data.sharedWith)
      when(persistenceService.getUser(user.username)).thenReturn(Future.successful(Some(user)))
      when(persistenceService.updateSecret(user.username, data))
        .thenReturn(Future.successful(Some(secret)))

      val creds = BasicHttpCredentials("user1", "pass1")
      Put("/secrets", data) ~> addCredentials(creds) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        entityAs[SelfSecret] shouldBe secret.toSelf
        verify(persistenceService).getUser(user.username)
        verify(persistenceService).updateSecret(user.username, data)
      }
    }
  }

  "GET /shared" should {
    "retrieve secrets shared with the user" in {
      val user = User("user1", "pass1", "user2", false, true)
      val secret = Secret("secret1", "secret text 1", "user2", user.username :: Nil)
      when(persistenceService.getUser(user.username)).thenReturn(Future.successful(Some(user)))
      when(persistenceService.getSecretsSharedWith(user.username))
        .thenReturn(Future.successful(secret :: Nil))

      val creds = BasicHttpCredentials("user1", "pass1")
      Get("/shared") ~> addCredentials(creds) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        entityAs[SharedSecrets] shouldBe SharedSecrets(secret.toShared :: Nil)
        verify(persistenceService).getUser(user.username)
        verify(persistenceService).getSecretsSharedWith(user.username)
      }
    }
  }
}
