package com.cleverbase.api

case class User(
    username: String,
    password: String,
    mate: String,
    isSuper: Boolean = false,
    isLoggedIn: Boolean = false
) {
  require(username != "", "username can't be empty")
  require(password != "", "password can't be empty")
  require(mate != "", "mate can't be empty")
  require(mate != username, "mate can't be self")

  def toShow = {
    ShowUser(username, mate, isSuper, isLoggedIn)
  }
}

case class ShowUser(
    username: String,
    mate: String,
    isSuper: Boolean = false,
    isLoggedIn: Boolean = false
) {
  require(username != "", "username can't be empty")
  require(mate != "", "mate can't be empty")
  require(mate != username, "mate can't be self")
}

case class CreateUser(username: String, password: String, mate: String, isSuper: Boolean = false) {
  require(username != "", "username can't be empty")
  require(password != "", "password can't be empty")
  require(mate != "", "mate can't be empty")
  require(mate != username, "mate can't be self")
}

case class ShowUsers(users: Seq[ShowUser])

case class Secret(id: String, text: String, owner: String, sharedWith: Seq[String]) {
  require(id != "", "id can't be empty")
  require(text != "", "text can't be empty")
  require(owner != "", "owner can't be empty")
}

case class CreateSecret(id: String, text: String) {
  require(id != "", "id can't be empty")
  require(text != "", "text can't be empty")
}

case class SelfSecret(id: String, text: String, sharedWith: Seq[String]) {
  require(id != "", "id can't be empty")
  require(text != "", "text can't be empty")
}

case class SelfSecrets(secrets: Seq[SelfSecret])

case class SharedSecret(id: String, text: String, owner: String) {
  require(id != "", "id can't be empty")
  require(text != "", "text can't be empty")
  require(owner != "", "owner can't be empty")
}

case class SharedSecrets(secrets: Seq[SharedSecret])

