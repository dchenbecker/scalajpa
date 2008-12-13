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

import _root_.javax.persistence._

import _root_.javax.naming.InitialContext
import _root_.javax.transaction.{Status,UserTransaction}

/**
 * This class represents an EntityManager that is retrieved from JNDI.
 *
 * @param jndiName The full JNDI binding for the managed EntityManager, such
 * as "java:comp/env/persistence/myem".
 * 
 * @author Derek Chen-Becker
 */
abstract class JndiEM(val jndiName : String) extends ScalaEMFactory {
  /**
   * Holds an InitialContext for use with JNDI.
   */
  lazy val ctxt = new InitialContext()

  /**
   * Returns the current UserTransaction retrieved from JNDI.
   */
  def tx = ctxt.lookup("java:comp/UserTransaction").asInstanceOf[UserTransaction]

  private val txStatus = Map(Status.STATUS_ACTIVE -> "ACTIVE",
		     Status.STATUS_COMMITTED -> "COMMITTED",
		     Status.STATUS_COMMITTING -> "COMMITTING",
		     Status.STATUS_MARKED_ROLLBACK -> "MARKED_ROLLBACK",
		     Status.STATUS_NO_TRANSACTION -> "NO_TRANSACTION",
		     Status.STATUS_PREPARED -> "PREPARED",
		     Status.STATUS_PREPARING -> "PREPARING",
		     Status.STATUS_ROLLING_BACK -> "ROLLING_BACK",
		     Status.STATUS_ROLLEDBACK -> "ROLLEDBACK",
		     Status.STATUS_UNKNOWN -> "UNKNOWN")

  /**
   * Retrieves an EntityManager via JNDI and associates it with a JTA transaction.
   */
  def openEM () = {
    // JNDI EMs want an existing JTA transaction
    tx.begin()

    val em = ctxt.lookup(jndiName).asInstanceOf[EntityManager]

    em
  }

  /**
   * Closes the given EntityManager and commits the transaction if it hasn't been marked
   * for rollback.
   */
  def closeEM (em : EntityManager) = {
    em.close()

    /* We only want to commit if we haven't already thrown an exception (due to a constraint violation, etc)
     */
    try {
      if (tx.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
	tx.rollback()
      } else if (tx.getStatus() == Status.STATUS_ACTIVE) {
	tx.commit()
      } else {
      }
    }
  }
}

