package com.cleverbase.api

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.mongodb.scala._
import org.mongodb.scala.model._
import org.mongodb.scala.model.Filters._

import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }

trait PersistenceService {

  def getUser(username: String): Future[Option[User]]
  def getUsers(): Future[Seq[User]]
  def createUser(data: CreateUser): Future[User]
  def clean(): Future[Unit]
}

class MongoPersistenceService(uri: String) extends PersistenceService {

  private val mongoClient = MongoClient(uri)
  private val codec = fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY)
  private val database = mongoClient.getDatabase("cleverbase").withCodecRegistry(codec)
  private val users: MongoCollection[User] = database.getCollection("users")

  def start: Future[String] = {
    users.createIndex(Indexes.ascending("username"), IndexOptions().unique(true)).toFuture()
  }

  def getUser(username: String): Future[Option[User]] = {
    users.find(equal("username", username)).toFuture().map(_.headOption)
  }

  def getUsers(): Future[Seq[User]] = {
    users.find().toFuture()
  }

  def createUser(data: CreateUser): Future[User] = {
    val user = User(
      username = data.username,
      password = data.password,
      mate = data.mate,
      isSuper = data.isSuper,
      isLoggedIn = false
    )
    users.insertOne(user).toFuture().map(_ => user)
  }

  def clean(): Future[Unit] = {
    users.drop().toFuture().map(_ => ())
  }
}