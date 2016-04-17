package controllers.model

import java.util.Date

class StockPrice(val date: Date,
                 val symbol: StockSymbol,
                 val price: BigDecimal,
                 val currency: Currency) {
}

object StockPrice {
  def apply(date: java.sql.Date,
            symbol: StockSymbol,
            price: BigDecimal,
            currency: Currency) =
    new StockPrice(date, symbol, price, currency)

  def apply(date: java.sql.Date,
            symbol: String,
            price: BigDecimal,
            currency: String) =
    new StockPrice(date, StockSymbol(symbol), price, Currency(currency))

  def fromTuple(record: (java.sql.Date, String, BigDecimal, String)): StockPrice = {
    StockPrice(record._1, record._2, record._3, record._4)
  }

  def toTuple(sp: StockPrice): Option[(java.sql.Date, String, BigDecimal, String)] = 
    Some((new java.sql.Date(sp.date.getTime), sp.symbol.toString, sp.price, sp.currency.toString))
}
