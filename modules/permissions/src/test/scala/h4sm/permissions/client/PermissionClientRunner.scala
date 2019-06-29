package h4sm
package permissions
package client

import cats.effect.{Bracket, Sync}

import auth.infrastructure.endpoint.UserRequest
import auth.client.AuthClientRunner
import cats.effect.{Bracket, Sync}
import cats.data.OptionT
import domain._
import infrastructure.endpoint.PermissionEndpoints
import infrastructure.repository._
import tsec.authentication.TSecBearerToken
import tsec.authentication.SecuredRequestHandler

abstract class PermissionClientRunner[F[_]: Sync: Bracket[?[_], Throwable]] extends AuthClientRunner[F] {
  implicit lazy val permRepo = new PermissionRepository(xa)
  implicit lazy val userPermRepo = new UserPermissionRepository(xa)
  lazy val permissionEndpoints = new PermissionEndpoints[F, TSecBearerToken](SecuredRequestHandler(auth))
  lazy val permissionClient = new PermissionClient(permissionEndpoints)

  def permitUser(ur: UserRequest, p: Permission): OptionT[F, PermissionId] = for {
    userDetails <- userAlg.byUsername(ur.username)
    permId <- permRepo.selectByAttributes(p.appName, p.name).map(_._2).orElse(permRepo.insertGetId(p))
    _ <- OptionT.liftF(userPermRepo.insert(UserPermission(userDetails._2, permId, userDetails._2)))
  } yield permId
}
