package h4sm.invitations.domain

import org.apache.commons.lang3.RandomStringUtils

object ShortCode {
  val DEFAULT_SIZE: Int = 8
  
  def generate(size: Int = DEFAULT_SIZE): String = RandomStringUtils.randomAlphanumeric(size)
}
