package h4sm.auth
package db.domain


final case class User(
  username: String,
  hash: Array[Byte]
)
