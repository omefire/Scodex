package controllers.datastore

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import javax.inject._
import java.sql.Date

import controllers.model._

class ExchangeRates @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private class ExchangeRateTable (tag: Tag) extends Table[ExchangeRate](tag, "EXCHANGE_RATE") {
    def date = column[Date]("DATE")
    def fromCurrency = column[String]("FROM_CURRENCY")
    def toCurrency = column[String]("TO_CURRENCY")
    def rate = column[BigDecimal]("RATE")
    def pk = primaryKey("pk_exchange_rate", (date, fromCurrency, toCurrency))
    def * = (date, fromCurrency, toCurrency, rate).shaped <> [ExchangeRate](ExchangeRate.fromTuple, ExchangeRate.toTuple _)
  }

  private val exchangeRates = TableQuery[ExchangeRateTable]
  def schema = exchangeRates.schema

  def all: Future[Seq[ExchangeRate]] = db.run(exchangeRates.result)

}
