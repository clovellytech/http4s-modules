package h4sm.featurerequests
package config

import h4sm.db.config.DatabaseConfig

final case class FeatureRequestConfig(host: String, port: Int, db: DatabaseConfig)
