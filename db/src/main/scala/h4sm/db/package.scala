package h4sm

import db.QueryFragment._

package object db {
  object implicits extends ToQueryFragmentOps with QueryFragmentInstances
}
