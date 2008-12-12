

package org.scalatools.jpatest

import javax.persistence.EntityManager
import org.scalatools.jpa.LocalEM
import net.liftweb.http.RequestVar
import net.liftweb.util.Full

object Model extends LocalEM("test") {
  object emVar extends RequestVar[EntityManager](openEM) {
    override def cleanupFunc = Full(() => closeEM(this.is))
  }

  protected def em = emVar.is

  def getEm = em
}
