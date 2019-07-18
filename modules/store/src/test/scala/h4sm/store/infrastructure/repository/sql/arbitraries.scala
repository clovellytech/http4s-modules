package h4sm.store
package infrastructure.repository.sql

import domain._
import h4sm.testutil.arbitraries._
import org.scalacheck._
import java.time.Instant

object arbitraries {

  implicit def orderItemArbitrary[A: Arbitrary]: Arbitrary[OrderItem0[A]] = Arbitrary {
    for {
      item <- implicitly[Arbitrary[A]].arbitrary
      quantity <- Gen.posNum[Int]
      orderPrice <- Gen.posNum[Double]
    } yield OrderItem0(item, quantity, orderPrice)
  }

  implicit def orderArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[Order0[A, B]] = Arbitrary {
    for {
      createBy <- implicitly[Arbitrary[A]].arbitrary
      // ItemId, quantity, price charged
      items <- Gen.listOf(orderItemArbitrary[B].arbitrary)
      submitDate <- Gen.option[Instant](implicitly[Arbitrary[Instant]].arbitrary)
      fulfilledDate <- Gen.option[Instant](implicitly[Arbitrary[Instant]].arbitrary)
      totalPrice <- Gen.posNum[Double]
    } yield Order(createBy, items, submitDate, fulfilledDate, totalPrice)
  }

  implicit def itemArbitrary[A: Arbitrary]: Arbitrary[Item0[A]] = Arbitrary {
    for {
      title <- nonEmptyString
      description <- nonEmptyString
      createBy <- implicitly[Arbitrary[A]].arbitrary
      price <- Gen.posNum[Double]
    } yield Item(title, description, createBy, price)
  }
}
