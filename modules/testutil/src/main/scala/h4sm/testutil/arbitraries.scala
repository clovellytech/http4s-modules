package h4sm.testutil

import java.time.Instant

import org.scalacheck.{Arbitrary, Gen}

object arbitraries{
  val nonEmptyString = Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString)

  implicit val arbInstant: Arbitrary[Instant] = Arbitrary {
    for {
      millis <- Gen.chooseNum(0L, Instant.MAX.getEpochSecond)
      nanos <- Gen.chooseNum(0L, Instant.MAX.getNano.toLong)
    } yield Instant.ofEpochMilli(millis).plusNanos(nanos)
  }

  def applyArb[A: Arbitrary, B](f : A => B) : B = f(implicitly[Arbitrary[A]].arbitrary.sample.get)
}
