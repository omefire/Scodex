package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import controllers.model._
import scala.util.{ Try, Success, Failure }

@Singleton
class UserController @Inject() extends Controller {

  def create = Action { request => 
    val tUser = request.body.asJson.flatMap { json =>
      for {
        username <- (json \ "username").asOpt[String]
        password <- (json \ "password").asOpt[String].map(Password(_))
      } yield createUser(username,
                         password,
                         (json \ "email").asOpt[String].map(Email(_)),
                         (json \ "telephoneNumber").asOpt[String].map(TelephoneNumber(_)))
    }
    tUser match {
      case Some(Success(user: User)) => Created(s"Created user: ${ user.username }")
      case Some(Failure(e: UserConflictException)) => Conflict(s"User exists: ${ e.getMessage }")
      case Some(Failure(e)) => BadRequest(s"Failed to create user: ${ e.getMessage }")
      case None => BadRequest(s"No content in request")
    }
  }

  def login = Action { request =>
    Ok(views.html.login.loginForm)
  }

  def authenticate = Action { implicit request =>
    /* Note: fold takes an errorFunction and a successFuction */
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(view.html.login(formWithErrors)),
      userTuple => Redirect(routes.Application.index).withSession(Security.username -> userTuple._1)
    )
  }

  def logout = Action { implicit request =>
    redirect(routes.Application.index).withNewSession.flashing(
      "success" -> "You are now logged out"
    )
  }

  val loginForm = Form(tuple("email" -> text,
                             "password" -> text)
                         verifying ("Invalid username or password",
                                    result => result match {
                                      case (username, password) =>
                                        authenticateUser(username, password)
                                    }))

  def createUser(username: String,
                 pw: PasswordHash,
                 email: Option[Email] = None,
                 telephoneNumber: Option[TelephoneNumber] = None): Try[User] = {
    val user = User(username, pw, email, telephoneNumber)
    UserStore.put(user)
  }

  def authenticateUser(username: String, password: String): Boolean = {
    UserStore.get(username) match {
      case Some(user) => user.password.compare(password)
      case None => false
    }
  }
}

trait SecuredController {
  def username(request: RequestHeader) = request.session.get(Security.username)
  def onUnauthorized(request: RequestHeader) = Forbidden("Access Denied.")
  def withAuthorization(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  def withAuthorizedUser(f: User => String => Request[AnyContent] => Result) = withAuthorization { username => implicit request =>
    UserStore.get(username) match {
      case Some(user) => f(user)(request)
      case None => onUnauthorized(request)
    }
  }
}
