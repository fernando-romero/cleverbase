package com.cleverbase.api

case class User(
    username: String,
    password: String,
    mate: String,
    isSuper: Boolean = false,
    isLoggedIn: Boolean = false
) {

  def toShow = {
    ShowUser(username, mate, isSuper, isLoggedIn)
  }
}

case class ShowUser(
  username: String,
  mate: String,
  isSuper: Boolean = false,
  isLoggedIn: Boolean = false
)

case class CreateUser(username: String, password: String, mate: String, isSuper: Boolean = false) {
  require(username != "", "username can't be empty")
  require(password != "", "password can't be empty")
  require(mate != "", "mate can't be empty")
  require(mate != username, "mate can't be self")
  require(Util.isAlphanumeric(username), "username must be alphanumeric")
  require(Util.isAlphanumeric(mate), "mate must be alphanumeric")
}

case class ShowUsers(users: Seq[ShowUser])

case class Secret(id: String, text: String, owner: String, sharedWith: Seq[String] = Nil) {

  def toSelf = {
    SelfSecret(id, text, sharedWith)
  }

  def toShared = {
    SharedSecret(id, text, owner)
  }
}

case class CreateSecret(id: String, text: String) {
  require(id != "", "id can't be empty")
  require(text != "", "text can't be empty")
  require(Util.isAlphanumeric(id), "id must be alphanumeric")

  def toSecret(owner: String) = {
    Secret(id, text, owner)
  }
}

case class SelfSecret(id: String, text: String, sharedWith: Seq[String])

case class SelfSecrets(secrets: Seq[SelfSecret])

case class SharedSecret(id: String, text: String, owner: String)

case class SharedSecrets(secrets: Seq[SharedSecret])

case class UpdateSecret(id: String, sharedWith: Seq[String])

