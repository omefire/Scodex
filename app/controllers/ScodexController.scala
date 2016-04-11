package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import controllers.model._
import scala.util.{ Try, Success, Failure }

@Singleton
class ScodexController @Inject() extends Controller {

  def user[T](request: Request[T]): Option[User] = {
    request.session.get(Security.username) match {
      case None => {
        None
      }
      case Some(username) => UserStore.get(username)
    }
  }

  def index = Action { implicit request =>
    Ok(views.html.index(user = user(request)))
  }

}

