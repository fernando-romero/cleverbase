package com.cleverbase.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._
  implicit val showUserFmt = jsonFormat4(ShowUser)
  implicit val showUsersFmt = jsonFormat1(ShowUsers)
  implicit val createUserFmt = jsonFormat4(CreateUser)
  implicit val createSecretFmt = jsonFormat2(CreateSecret)
  implicit val selfSecretFmt = jsonFormat3(SelfSecret)
  implicit val selfSecretsFmt = jsonFormat1(SelfSecrets)
  implicit val sharedSecretFmt = jsonFormat3(SharedSecret)
  implicit val sharedSecretsFmt = jsonFormat1(SharedSecrets)
}
