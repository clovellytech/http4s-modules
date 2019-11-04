package h4sm.testutil

import org.scalacheck.{Arbitrary, Gen}

trait ArbitraryUtil {
  val nonEmptyString = Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString)

  def applyArb[A: Arbitrary, B](f: A => B): B = f(implicitly[Arbitrary[A]].arbitrary.sample.get)
}
