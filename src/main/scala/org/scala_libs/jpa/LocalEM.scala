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
package org.scala_libs.jpa

import javax.persistence.{EntityManager,Persistence}

/**
 * <p>This class represents an EntityManager factory that is retrieved via the
 * javax.persistence.Persistence factory class. Typically this is
 * used in an environment that doesn't support JNDI. An example of
 * usage would be:</p>
 *
 * <pre>
 * object FooEM extends LocalEMF("foo")
 *
 * ...
 *
 * def doSomething = {
 *   val em = FooEM.newEM
 *   ...
 *   em.close()
 * }
 * </pre>
 *
 * @author Derek Chen-Becker
 *
 * @param unitName The persistence unit name that this EM should represent
 * @param userTx controls whether the user is responsible for handling transactions.
 * <code>true</code> means that the user will begin and end transactions,
 * <code>false</code> means that the LocalEM will handle it for the user.
 */
class LocalEMF(val unitName : String, val userTx : Boolean, properties : Option[Map[_,_]]) extends ScalaEMFactory {
  /**
   * Creates a new EM manager with the specified transaction management and
   * configuration properties. This is a convenience constructor so that you're
   * not required to pass an Option.
   * 
   * @param unitName The persistence unit name that this EM should represent
   * @param userTx controls whether the user is responsible for handling transactions.
   * <code>true</code> means that the user will begin and end transactions,
   * <code>false</code> means that the LocalEM will handle it for the user.
   * @param properties A map containing additional properties to use when creating
   * the factory.
   */
  def this(unitName : String, userTx : Boolean, properties : Map[_,_]) = 
    this(unitName, userTx, Some(properties))
  
  /**
   * Creates a new EM manager with the specified transaction management.
   * 
   *  * @param unitName The persistence unit name that this EM should represent
   * @param userTx controls whether the user is responsible for handling transactions.
   * <code>true</code> means that the user will begin and end transactions,
   * <code>false</code> means that the LocalEM will handle it for the user.
   */
  def this(unitName : String, userTx : Boolean) = this(unitName, userTx, None)
  
  /**
   * Creates a new EM manager that handles its own transactions.
   * 
   * @param unitName The persistence unit name that this EM should represent
   */
  def this(unitName : String) = this(unitName, false, None)

  // The underlying entitymanager factory
  private val emf = properties match {
    case None => Persistence.createEntityManagerFactory(unitName)
    case Some(props) => Persistence.createEntityManagerFactory(unitName, unmap(props))
  }
  
  private def unmap[A,B](input : Map[A,B]) : java.util.Map[A,B] = {
    val output = new java.util.HashMap[A,B]()
    
    input.keys.foreach { key => output.put(key, input(key))}
    
    output
  }

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

  def getUnitName = unitName

  /**
   * Closes the underlying EMF. This should be called to release any held
   * resources to prevent memory leaks.
   */
  def shutdownEMF = emf.close()
}

