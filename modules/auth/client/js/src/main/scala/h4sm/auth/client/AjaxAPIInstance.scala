package h4sm.auth.client

import h4sm.common.AjaxAPI
import scala.concurrent.ExecutionContext

trait AjaxAPIInstance {
  implicit def apiInstance(implicit ecc: ExecutionContext) =
    new AjaxAPI {
      override def threadHeaders: Set[String] = Set("Authorization")
    }
}
