package h4sm.auth
package domain.tokens

import h4sm.db.CRUDService
import tsec.common.SecureRandomId

class TokenService[F[_]](val algebra : TokenRepositoryAlgebra[F])
extends CRUDService[F, SecureRandomId, BearerToken, Unit]{
  type Alg = TokenRepositoryAlgebra[F]
}
