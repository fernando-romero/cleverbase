package com.cleverbase.api

import scala.concurrent.Future

trait PersistenceService {

  def getUser(username: String): Future[Option[User]]
}

class MemoryPersistenceService extends PersistenceService {

  val users = User("foo", "bar") :: User("bar", "foo") :: Nil

  def getUser(username: String): Future[Option[User]] = {
    Future.successful(users.find(_.username == username))
  }
}