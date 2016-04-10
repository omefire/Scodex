package controllers.model

case class User(username: String,
                password: PasswordHash,
                email: Option[Email] = None,
                telephoneNumber: Option[TelephoneNumber] = None) {
}
