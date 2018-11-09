package h4sm.auth
package domain.tokens

import h4sm.db.CRUDAlgebra
import tsec.common.SecureRandomId


trait TokenRepositoryAlgebra[F[_]] extends CRUDAlgebra[F, SecureRandomId, BearerToken, Unit]
