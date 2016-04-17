package controllers.model.exception

class UserConflictException(
  username: String,
  message: String = null,
  cause: Throwable = null) extends RuntimeException(message, cause) {}
