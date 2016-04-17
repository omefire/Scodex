package controllers.datastore

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import javax.inject._

import controllers.model._

class Users @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  
  private class UserTable (tag: Tag) extends Table[User](tag, "USERS") {
    def username = column[String]("USERNAME", O.PrimaryKey)
    def passwordHash = column[String]("PASSWORD")
    def email = column[Option[String]]("EMAIL")
    def phoneNumber = column[Option[String]]("PHONE_NUMBER")
    def * = (username, passwordHash, email, phoneNumber).shaped <> [User](User.fromTuple, User.toTuple _)
  }

  // private object Users extends Table[User]("USER") {
  //   def username = column[String]("USERNAME", O.PrimaryKey)
  //   def passwordHash = column[String]("PASSWORD")
  //   def email = column[Option[String]]("EMAIL")
  //   def phoneNumber = column[Option[String]]("PHONE_NUMBER")
  //   def * = (username, passwordHash, email, phoneNumber).shaped <> [User](User.fromTuple, User.toTuple _)
  // }

  private val users = TableQuery[UserTable]

  def all: Future[Seq[User]] = db.run(users.result)

  def insert(user: User): Try[User] = Try {
    db.run(users += user)
    user
  }

  def get(username: String): Future[Option[User]] =
    db.run(users.filter(user => user.username === username).result.headOption)

  def += (user: User): Try[User] = insert(user)
}
