package controllers.action

import play.api.mvc._
import controllers.model._
import scala.concurrent.Future
import play.api.mvc.Results._

class UserRequest[T](val user: Option[User], request: Request[T]) extends WrappedRequest[T](request)

object UserAction extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest] {
  def transform[T](request: Request[T]): Future[UserRequest[T]] = Future.successful {
    new UserRequest(request.session.get(Security.username).flatMap(UserStore.get(_)), request)
  }
}

object AuthenticatedAction extends ActionFilter[UserRequest] {
  def filter[T](request: UserRequest[T]): Future[Option[Result]] = Future.successful {
    if (request.user.isEmpty) {
      Some(Forbidden)
    } else {
      None
    }
  }
}
