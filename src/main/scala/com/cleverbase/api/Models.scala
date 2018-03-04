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
  require(username.length() < 21, "username can't be longer than 20 characters")
  require(password.length() < 21, "password can't be longer than 20 characters")
  require(mate.length() < 21, "mate can't be longer than 20 characters")
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
  require(username.length() < 21, "username can't be longer than 20 characters")
  require(mate.length() < 21, "mate can't be longer than 20 characters")
  require(mate != username, "mate can't be self")
}

case class CreateUser(username: String, password: String, mate: String, isSuper: Boolean = false) {
  require(username != "", "username can't be empty")
  require(password != "", "password can't be empty")
  require(mate != "", "mate can't be empty")
  require(username.length() < 21, "username can't be longer than 20 characters")
  require(password.length() < 21, "password can't be longer than 20 characters")
  require(mate.length() < 21, "mate can't be longer than 20 characters")
  require(mate != username, "mate can't be self")
}