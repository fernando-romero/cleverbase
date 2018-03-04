package com.cleverbase.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._
  implicit val showUserFmt = jsonFormat4(ShowUser)
  implicit val createUserFmt = jsonFormat4(CreateUser)
}
