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

class StockPrices @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private class StockPriceTable (tag: Tag) extends Table[StockPrice](tag, "STOCK_PRICE") {
    def date = column[Date]("DATE")
    def symbol = column[String]("SYMBOL")
    def price = column[BigDecimal]("PRICE")
    def currency = column[String]("CURRENCY")
    def pk = primaryKey("pk_stock_price", (date, symbol))
    def * = (date, symbol, price, currency).shaped <> [StockPrice](StockPrice.fromTuple, StockPrice.toTuple _)
  }

  private val stockPrices = TableQuery[StockPriceTable]
  def schema = stockPrices.schema

  def all: Future[Seq[StockPrice]] = db.run(stockPrices.result)

}
