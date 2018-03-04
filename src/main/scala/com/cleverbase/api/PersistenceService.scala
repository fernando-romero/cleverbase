package com.cleverbase.api

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.mongodb.scala._
import org.mongodb.scala.model._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.Indexes._

import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }

trait PersistenceService {

  def getUser(username: String): Future[Option[User]]
  def getUsers(): Future[Seq[User]]
  def createUser(data: CreateUser): Future[User]
  def deleteUsers(): Future[Unit]
  def createSecret(owner: String, data: CreateSecret): Future[Secret]
  def deleteSecrets(): Future[Unit]
  def getSecretsByOwner(owner: String): Future[Seq[Secret]]
  def updateSecret(owner: String, data: UpdateSecret): Future[Option[Secret]]
  def getSecretsSharedWith(username: String): Future[Seq[Secret]]
  def login(user: User, secret: Option[String]): Future[Boolean]
}

class MongoPersistenceService(uri: String) extends PersistenceService {

  private val mongoClient = MongoClient(uri)
  private val codec = fromRegistries(fromProviders(classOf[User], classOf[Secret]), DEFAULT_CODEC_REGISTRY)
  private val database = mongoClient.getDatabase("cleverbase").withCodecRegistry(codec)
  private val users: MongoCollection[User] = database.getCollection("users")
  private val secrets: MongoCollection[Secret] = database.getCollection("secrets")

  def start: Future[Unit] = {
    val secretsIndex = compoundIndex(ascending("id"), ascending("owner"))
    for {
      _ <- users.createIndex(ascending("username"), IndexOptions().unique(true)).toFuture()
      _ <- secrets.createIndex(secretsIndex, IndexOptions().unique(true)).toFuture()
    } yield {
      ()
    }
  }

  def getUser(username: String): Future[Option[User]] = {
    users.find(equal("username", username)).toFuture().map(_.headOption)
  }

  def getUsers(): Future[Seq[User]] = {
    users.find().toFuture()
  }

  def createUser(data: CreateUser): Future[User] = {
    val user = data.toUser
    users.insertOne(user).toFuture().map(_ => user)
  }

  def deleteUsers(): Future[Unit] = {
    users.drop().toFuture().map(_ => ())
  }

  def createSecret(owner: String, data: CreateSecret): Future[Secret] = {
    val secret = data.toSecret(owner)
    secrets.insertOne(secret).toFuture().map(_ => secret)
  }

  def deleteSecrets(): Future[Unit] = {
    secrets.drop().toFuture().map(_ => ())
  }

  def getSecretsByOwner(owner: String): Future[Seq[Secret]] = {
    secrets.find(equal("owner", owner)).toFuture()
  }

  def updateSecret(owner: String, data: UpdateSecret): Future[Option[Secret]] = {
    val filter = and(equal("id", data.id), equal("owner", owner))
    secrets
      .updateOne(filter, set("sharedWith", data.sharedWith))
      .toFuture()
      .flatMap(_ => secrets.find(equal("id", data.id)).toFuture().map(_.headOption))
  }

  def getSecretsSharedWith(username: String): Future[Seq[Secret]] = {
    secrets.find(equal("sharedWith", username)).toFuture()
  }

  private def setAsLoggedIn(username: String) = {
    users.updateOne(equal("username", username), set("isLoggedIn", true)).toFuture()
  }

  private def verifySecret(user: User, secret: String): Future[Boolean] = {
    val filter = and(
      equal("owner", user.mate),
      equal("sharedWith", user.username),
      equal("text", secret)
    )
    secrets
      .find(filter)
      .toFuture()
      .map(_.size > 0)
  }

  def login(user: User, secret: Option[String]): Future[Boolean] = {
    if (user.isLoggedIn) {
      Future.successful(true)
    } else if (user.isSuper) {
      setAsLoggedIn(user.username).map(_ => true)
    } else {
      secret match {
        case None =>
          Future.successful(false)
        case Some(s) =>
          verifySecret(user, s).flatMap {
            case true =>
              setAsLoggedIn(user.username).map(_ => true)
            case false =>
              Future.successful(false)
          }
      }
    }
  }
}
