package controllers.datastore

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.dbio.DBIO
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import javax.inject._

class DataStore @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                          private val users: Users,
                          private val stockPrices: StockPrices,
                          private val exchangeRates: ExchangeRates)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  // val initializer = DBIO.seq(users.schema.create,
  //                            stockPrices.schema.create,
  //                            exchangeRates.schema.create)

  // def initialize = db.run(initializer)
  
}
