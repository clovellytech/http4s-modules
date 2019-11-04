package h4sm.auth.domain

package object tokens 
  extends AsBaseTokenInstances
  with BaseTokenReaderInstances {
    object tokenInstances extends AsBaseTokenInstances
    object tokenReaderInstances extends BaseTokenReaderInstances
  }
