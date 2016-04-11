package controllers.store

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future
import javax.inject._

import controllers.model._

class UserStore @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Users = TableQuery[UsersTable]

  def all: Future[Seq[User]] = db.run(Users.result)

  def insert(user: User) = db.run(Users += user).map { _ => () }

  private class UsersTable (tag: Tag) extends Table[(String, String, String, String)](tag, "USERS") {
    def username = column[String]("USERNAME", O.PrimaryKey)
    def passwordHash = column[String]("PASSWORD")
    def email = column[Option[String]]("EMAIL")
    def phoneNumber = column[Option[String]]("PHONE_NUMBER")
    def * = (username, passwordHash, email, phoneNumber) <> (User.stringTupled, User.extractStringTuple _)
  }
}

