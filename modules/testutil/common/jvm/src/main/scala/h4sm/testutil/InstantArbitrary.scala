package h4sm.testutil

import java.time.Instant

import org.scalacheck.{Arbitrary, Gen}

trait InstantArbitrary {
  val instant = for {
    millis <- Gen.chooseNum(0L, Instant.MAX.getEpochSecond)
    nanos <- Gen.chooseNum(0L, Instant.MAX.getNano.toLong)
  } yield Instant.ofEpochMilli(millis).plusNanos(nanos)

  implicit val arbInstant: Arbitrary[Instant] = Arbitrary(instant)
}
