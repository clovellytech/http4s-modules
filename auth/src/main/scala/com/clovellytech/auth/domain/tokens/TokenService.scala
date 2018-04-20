package com.clovellytech.auth
package domain.tokens

import com.clovellytech.db.CRUDService
import tsec.common.SecureRandomId

class TokenService[F[_]](val algebra : TokenRepositoryAlgebra[F])
extends CRUDService[F, SecureRandomId, BearerToken, Unit]{
  type Alg = TokenRepositoryAlgebra[F]
}
