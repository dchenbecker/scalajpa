/*
 * Copyright 2008 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.scalatools.jpa

import _root_.javax.persistence.{EntityManager,Persistence}

/**
 * This class represents an EntityManager that is retrieved via the
 * javax.persistence.Persistence factory class. Typically this is
 * used in an environment that doesn't support JNDI.
 */
abstract class LocalEM(val name : String, val userTx : Boolean) extends ScalaEntityManager {
  
  /**
   * Creates a new EM manager that handles its own transactions.
   */
  def this(nm : String) = this(nm, false)

  // The underlying entitymanager factory
  val emf = Persistence.createEntityManagerFactory(name)

  override def openEM () = {
    val em = emf.createEntityManager()

    if (!userTx) {
      em.getTransaction.begin()
    }

    em
  }

  override def closeEM (em : EntityManager) = {
    if (!userTx) {
      if (em.getTransaction.getRollbackOnly) {
	em.getTransaction.rollback()
      } else {
	em.getTransaction.commit()
      }
    }

    em.close()
  }
}

