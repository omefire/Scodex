package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.i18n.{ I18nSupport, MessagesApi }
import controllers.datastore.Users
import controllers.model._
import scala.util.{ Try, Success, Failure }
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._

@Singleton
class ScodexController @Inject()(val messagesApi: MessagesApi, val users: Users) extends Controller {

  def user[T](request: Request[T]): Option[User] = for {
      username <- request.session.get(Security.username)
      user <- Await.result(users.get(username), 2000 millis)
  } yield user

  def index = Action { implicit request =>
    Ok(views.html.index(user = user(request)))
  }

}

