package controllers.model

class User(val username: String,
           val passwordHash: PasswordHash,
           val email: Option[Email] = None,
           val phoneNumber: Option[PhoneNumber] = None) {
}

object User {
  def apply(username: String,
            passwordHash: String,
            email: Option[String],
            phoneNumber: Option[String]): User = {
    new User(username, PasswordHash(passwordHash), email.map(Email(_)), phoneNumber.map(PhoneNumber(_)))
  }

  def apply(username: String,
            passwordHash: PasswordHash,
            email: Option[Email],
            phoneNumber: Option[PhoneNumber]): User = {
    new User(username, passwordHash, email, phoneNumber)
  }

  def stringTupled = ((username: String, passwordHash: String, email: Option[String], phoneNumber: Option[String])) => {
    new User(username, PasswordHash(passwordHash), email.map(Email(_)), phoneNumber.map(PhoneNumber(_)))
  }

  def extractStringTuple(user: User) = (user.username, user.passwordHash.pwHash, user.email.map(_.email), user.phoneNumber.map(_.number))
}
