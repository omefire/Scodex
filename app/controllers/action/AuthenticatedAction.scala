package controllers.action

import play.api.mvc._
import controllers.model._
import scala.concurrent.{ Future, Await }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import play.api.mvc.Results._

import javax.inject._

class AuthenticatedRequest[T](val user: Option[User], request: Request[T]) extends WrappedRequest[T](request)

trait AuthenticatedActions {
  val users: controllers.datastore.Users

  def AuthenticatedAction = new ActionBuilder[AuthenticatedRequest] {
    override def invokeBlock[T](request: Request[T], block: AuthenticatedRequest[T] => Future[Result]): Future[Result] = {
      request.session.get(Security.username).flatMap { username =>
        Await.result(users.get(username), 2000 millis)
      } match {
        case None => Future { Forbidden }
        case Some(user) => block(new AuthenticatedRequest(Some(user), request))
      }
    }
  }
}

