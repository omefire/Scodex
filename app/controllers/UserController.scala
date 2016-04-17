package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.i18n.{ I18nSupport, MessagesApi }

import play.api.data.validation._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._

// import play.api.Play.current
// import play.api.i18n.Messages.Implicits._

import scala.util.{ Try, Success, Failure }

import controllers.model._
import controllers.datastore.Users

@Singleton
class UserController @Inject()(val messagesApi: MessagesApi, val users: Users) extends Controller with I18nSupport {

  def user[T](request: Request[T]): Option[User] = for {
      username <- request.session.get(Security.username)
      user <- Await.result(users.get(username), 2000 millis)
  } yield user

  def create = Action { request =>
    val tUser = request.body.asJson.flatMap { json =>
      for {
        username <- (json \ "username").asOpt[String]
        password <- (json \ "password").asOpt[String].map(Password(_))
      } yield createUser(username,
                         password,
                         (json \ "email").asOpt[String].map(Email(_)),
                         (json \ "telephoneNumber").asOpt[String].map(PhoneNumber(_)))
    }
    tUser match {
      case Some(Success(user: User)) => Created(s"Created user: ${ user.username }")
      case Some(Failure(e: UserConflictException)) => Conflict(s"User exists: ${ e.getMessage }")
      case Some(Failure(e)) => BadRequest(s"Failed to create user: ${ e.getMessage }")
      case None => BadRequest(s"No content in request")
    }
  }

  def login = Action { request =>
    Ok(views.html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    /* Note: fold takes an errorFunction and a successFuction */
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      loginData => {
        if (authenticateUser(loginData.username, loginData.password)) {
          Redirect(routes.ScodexController.index).withSession(Security.username -> loginData.username)
        } else {
          Forbidden(views.html.login(loginForm))
        }
      }
    )
  }

  def registration = Action { request =>
    Ok(views.html.registration(userRegistrationForm))
  }

  def list = Action { request =>
    Await.result(users.all.map { users => Ok(views.html.list(users.toSet)) }, 2000 millis)
  }

  def register = Action { implicit request =>
    userRegistrationForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.registration(formWithErrors)),
      userData => {
        val user = createUser(userData)
        user match {
          case Success(user) => Redirect(routes.ScodexController.index).withSession(Security.username -> userData.username)
          case Failure(e) => BadRequest(s"Failed to create user: ${ e.getMessage }")
        }
      })
  }

  def logout = Action { implicit request =>
    Redirect(routes.ScodexController.index).withNewSession.flashing(
      "success" -> "You are now logged out"
    )
  }

  val loginForm = Form(mapping("username" -> nonEmptyText,
                               "password" -> nonEmptyText)
                              (LoginData.apply)
                              (LoginData.unapply) verifying(
    "Bad username or password!",
    fields => fields match {
      case loginData => authenticateUser(loginData.username, loginData.password)
    }))

  val userRegistrationForm = Form(mapping("username" -> nonEmptyText,
                                          "password" -> nonEmptyText,
                                          "email" -> optional(email),
                                          "phoneNumber" -> optional(nonEmptyText))
                                         (UserRegistrationData.apply)
                                         (UserRegistrationData.unapply) verifying(
                                           "User already exists!",
                                           fields => fields match {
                                             case userData => Await.result(users.get(userData.username).map(_.isEmpty), 2000 millis)
                                           }))

  private def createUser(username: String,
                         pw: PasswordHash,
                         email: Option[Email] = None,
                         telephoneNumber: Option[PhoneNumber] = None): Try[User] = {
    val user = User(username, pw, email, telephoneNumber)
    users += user
  }

  private def createUser(userData: UserRegistrationData): Try[User] =
    createUser(userData.username, Password(userData.password), userData.email.map(Email(_)), userData.phoneNumber.map(PhoneNumber(_)))

  private def authenticateUser(username: String, password: String): Boolean = {
    Await.result(users.get(username).map { maybeUser =>
      maybeUser.map(user => user.passwordHash.compare(password)) == Some(true)
    }, 2000 millis)
  }
}

case class LoginData (username: String, password: String) {}
case class UserRegistrationData (username: String,
                                 password: String,
                                 email: Option[String],
                                 phoneNumber: Option[String]) {}

