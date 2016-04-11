package controllers.model

import scala.util.{ Try, Success, Failure }

class UserConflictException(
  username: String,
  message: String = null,
  cause: Throwable = null) extends RuntimeException(message, cause) {}

class UserStore() {
  val users = collection.mutable.Map[String, User]()

  def get(username: String): Option[User] = {
    users.get(username)
  }

  def put(user: User): Try[User] = {
    if (users contains user.username) {
      Failure(new UserConflictException(user.username,
                                        message = s"User ${ user.username } already exists!"))
    } else {
      users put (user.username, user)
      Success(user)
    }
  }

  def getAll: Set[User] = {
    users.values.toSet
  }
}

object UserStore {
  val store = new UserStore

  def get(username: String) = store.get(username)
  def put(user: User) = store.put(user)
  def getAll: Set[User] = store.getAll
}

