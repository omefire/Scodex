package controllers.model

class User(val username: String,
           val passwordHash: PasswordHash,
           val email: Option[Email] = None,
           val phoneNumber: Option[PhoneNumber] = None) {
}

object User {
  def apply(username: String,
            passwordHash: String,
            email: Option[String] = None,
            phoneNumber: Option[String] = None): User = {
    new User(username, PasswordHash(passwordHash), email.map(Email(_)), phoneNumber.map(PhoneNumber(_)))
  }

  def apply(username: String,
            passwordHash: PasswordHash,
            email: Option[Email],
            phoneNumber: Option[PhoneNumber]): User = {
    new User(username, passwordHash, email, phoneNumber)
  }

  def fromTuple(record: (String, String, Option[String], Option[String])): User = {
    new User(record._1, PasswordHash(record._2), record._3.map(Email(_)), record._4.map(PhoneNumber(_)))
  }

  def toTuple(user: User): Option[(String, String, Option[String], Option[String])] =
    Some((user.username, user.passwordHash.pwHash, user.email.map(_.email), user.phoneNumber.map(_.number)))
}
