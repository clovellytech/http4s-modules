package h4sm.db

import doobie._
import simulacrum._

@typeclass
trait QueryFragment[A]{
  @op("toFragment")
  def toFragment(self: A): Fragment
}

trait QueryFragmentInstances {
  implicit def qfragment[A] : QueryFragment[Query0[A]] = new QueryFragment[Query0[A]] {
    def toFragment(self: Query0[A]): Fragment = Fragment(self.sql, Nil, self.pos)
  }
}
