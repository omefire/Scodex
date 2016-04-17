package controllers.model

import java.util.Date

class ExchangeRate(val date: Date,
                   val fromCurrency: Currency,
                   val toCurrency: Currency,
                   val rate: BigDecimal) {
}

object ExchangeRate {
  def apply(date: java.sql.Date,
            fromCurrency: String,
            toCurrency: String,
            rate: BigDecimal) =
    new ExchangeRate(date,
                     Currency(fromCurrency),
                     Currency(toCurrency),
                     rate)

  def apply(date: Date,
            fromCurrency: Currency,
            toCurrency: Currency,
            rate: BigDecimal) = 
    new ExchangeRate(date,
                     fromCurrency,
                     toCurrency,
                     rate)

  def fromTuple(record: (java.sql.Date, String, String, BigDecimal)): ExchangeRate = {
    ExchangeRate(record._1, record._2, record._3, record._4)
  }

  def toTuple(ex: ExchangeRate): Option[(java.sql.Date, String, String, BigDecimal)] = {
    Some((new java.sql.Date(ex.date.getTime), ex.fromCurrency.toString, ex.toCurrency.toString, ex.rate))
  }
}
