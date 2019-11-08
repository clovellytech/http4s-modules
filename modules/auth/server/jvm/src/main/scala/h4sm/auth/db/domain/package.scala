package h4sm.auth.db

import java.util.UUID

import tsec.authentication.AuthenticatedCookie

package object domain {
  type Cookie[Alg] = AuthenticatedCookie[Alg, UUID]
}
