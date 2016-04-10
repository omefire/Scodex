package controllers.model

import org.mindrot.jbcrypt.BCrypt

class PasswordHash(val pwHash: String) {
  def compare(password: String): Boolean = BCrypt.checkpw(password, pwHash)
}

object Password {
  def apply(password: String) = {
    val pwHash = BCrypt.hashpw(password, BCrypt.gensalt())
    new PasswordHash(pwHash)
  }
}

object PasswordHash {
  def apply(passwordHash: String) = new PasswordHash(passwordHash)
}
