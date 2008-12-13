/*
 * Copyright 2008 Derek Chen-Becker
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
 * used in an environment that doesn't support JNDI. The userTx param
 * 
 *
 * @author Derek Chen-Becker
 *
 * @param unitName The persistence unit name that this EM should represent
 * @param userTx controls whether the user is responsible for handling transactions.
 * <code>true</code> means that the user will begin and end transactions,
 * <code>false</code> means that the LocalEM will handle it for the user.
 */
class LocalEM(val unitName : String, val userTx : Boolean) extends ScalaEMFactory {
  
  /**
   * Creates a new EM manager that handles its own transactions.
   * 
   * @param unitName The persistence unit name that this EM should represent
   */
  def this(unitName : String) = this(unitName, false)

  // The underlying entitymanager factory
  private val emf = Persistence.createEntityManagerFactory(unitName)

  /**
   * Opens an <code>EntityManager</code> retrieved from the <code>EntityManagerFactory</code>
   * for the given persistence unit.
   */
  def openEM () = {
    val em = emf.createEntityManager()

    if (!userTx) {
      em.getTransaction.begin()
    }

    em
  }

  /**
   * Closes the underlying EM and handles the transaction if configured
   * to do so.
   */
  def closeEM (em : EntityManager) = {
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

