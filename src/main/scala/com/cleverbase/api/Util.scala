package com.cleverbase.api

object Util {
  private val alphanums = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toSet
  def isAlphanumeric(s: String) = s.forall(alphanums.contains(_))
}