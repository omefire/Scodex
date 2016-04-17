package controllers.model

sealed abstract class Currency
case object USD extends Currency
case object EUR extends Currency
case object GBP extends Currency

object Currency {
  def apply(currency: String): Currency = fromString(currency)

  def fromString(currency: String): Currency = currency match {
    case "USD" => USD
    case "EUR" => EUR
    case "GBP" => GBP
    case _ => throw new IllegalArgumentException(s"Unrecognied currency: ${ currency }")
  }
}
