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
}

class MemoryPersistenceService extends PersistenceService {

  val users = User("foo", "bar") :: User("bar", "foo") :: Nil

  def getUser(username: String): Future[Option[User]] = {
    Future.successful(users.find(_.username == username))
  }
}

class MongoPersistenceService(uri: String) extends PersistenceService {

  private val mongoClient = MongoClient(uri)
  private val codec = fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY)
  private val database = mongoClient.getDatabase("cleverbase").withCodecRegistry(codec)
  private val users: MongoCollection[User] = database.getCollection("users")

  def start: Future[String] = {
    for {
      _ <- users.insertOne(User("fernando", "romero")).toFuture().recover { case _ => Nil }
      _ <- users.insertOne(User("remco", "vanwijk")).toFuture().recover { case _ => Nil }
      r <- users.createIndex(Indexes.ascending("username"), IndexOptions().unique(true)).toFuture()
    } yield {
      r
    }
  }

  def getUser(username: String): Future[Option[User]] = {
    users.find(equal("username", username)).toFuture().map(_.headOption)
  }
}