package json.collection

import java.net.URI

case class JsonCollection(version: Version = Version.ONE,
                          href: URI,
                          links: Seq[Link],
                          items: Seq[Item],
                          queries: Seq[Query],
                          template: Option[Template],
                          error: Option[ErrorMessage]
                          )

object JsonCollection {

  def apply(href: URI, error: ErrorMessage):JsonCollection =
    JsonCollection(Version.ONE, href, Nil, Nil, Nil, None, Some(error))

  def apply(href: URI,
            links: Seq[Link],
            items: Seq[Item],
            template: Option[Template],
            queries: Seq[Query]):JsonCollection =
    JsonCollection(Version.ONE, href, links, items, queries, template, None)

  def apply(href: URI,
            links: Seq[Link],
            items: Seq[Item],
            queries: Seq[Query]):JsonCollection =
    JsonCollection(Version.ONE, href, links, items, queries, None, None)

  def apply(href: URI,
            links: Seq[Link],
            items: Seq[Item]):JsonCollection =
    JsonCollection(Version.ONE, href, links, items, Nil, None, None)

}

sealed class Version(id:String)

object Version {
  def apply(id: String) = id match {
    case "1.0" => ONE
    case _ => ONE
  }

  case object ONE extends Version("1.0")
}

sealed trait Property {
  def name : String
  def prompt : Option[String]
}

case class PropertyWithValue[A](name: String, prompt: Option[String], value: A) extends Property
case class PropertyWithoutValue(name: String, prompt: Option[String]) extends Property

case class ErrorMessage(title: String, code: Option[String], message: Option[String])

sealed abstract class Render(name: String)

object Render {
  case object IMAGE extends Render("image")
  case object LINK extends Render("link")

  def apply(value: String): Option[Render] = value match {
    case "image" => Some(IMAGE)
    case "link" => Some(LINK)
    case _ => None
  }
}

sealed trait Value[A] {
  def value: A
}

object Value {
  def apply(any: Any) = any match {
    case x: String => StringValue(x)
    case x: Boolean => BooleanValue(x)
    case x: Numeric[_] => NumericValue(x)
    case null => NullValue
  }
}

case class StringValue(value: String) extends Value[String]

case class NumericValue(value: Numeric[_]) extends Value[Numeric[_]]

case class BooleanValue(value: Boolean) extends Value[Boolean]

case object NullValue extends Value[Null] {
  def value = null
}

case class Link(href: URI, rel: String, prompt: Option[String] = None, render: Render = Render.LINK)
case class Item(href: URI, properties: Seq[Property], links: Seq[Link])
case class Query(href: URI, rel: String, prompt: Option[String], properties: Seq[Property])
case class Template(properties: Seq[Property])

object Conversions {
  implicit def stringToValue(value: String)   = Some(Value(value))
  implicit def numericToValue(value: Numeric[_]) = Some(Value(value))
  implicit def booleanToValue(value: Boolean) = Some(Value(value))
  implicit def nullToValue(value: Null) = Some(Value(value))

  //implicit def valueToType[A](value: Option[Value[A]]) = value.map(_.value)
}
