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

/**
 * <p>
 * This trait represents a Scalafied version of the standard EntityManager
 * in JPA. In particular, queries are more type-safe and the find method
 * returns an Option instead of possibly throwing an exception if the
 * entity isn't found.
 * </p>
 *
 * <p>
 * If you wish to provide a concrete implementation of this trait, you
 * only need to provide the <code>em</code> method and <code>factory</code> val.
 * </p>
 *
 * @author Derek Chen-Becker
 */
trait ScalaEntityManager {
  /**
   * Returns the current EntityManager instance. This leaves the
   * implementor free to choose how they want to manage instances. For
   * example, the implementor could use a ThreadLocal to allow for a
   * singleton instance of ScalaEntityManager, or they could simply
   * set up a val to hold the current instance.
   */
  protected def em : EntityManager

  /**
   * This val should hold a reference to the factory that created
   * this instance, so that the proper closeEM method can be called
   * on shutdown.
   */
  val factory : ScalaEMFactory

  // value added methods
   
  /**
   * <p>
   * Returns a <code>List[A]</code> of the results of excuting the given query.
   * Named parameters
   * may be provided to refine the query. Using Scala's syntax sugar for
   * Pairs and varargs, an example query might look like:
   * </p>
   *
   * <p>
   * <code>
   *   EM.findAll[User]("byUsername", "username" -> "fred")
   * </code>
   * </p>
   *
   * @param queryName The name of the query to execute
   * @param params Zero or more pairs of (paramName,paramValue)
   *
   * @return A List[A] representing the results of the query
   * 
   */
  def findAll[A](queryName : String, params : Pair[String,Any]*) = createAndParamify[A](queryName, params).findAll

  /**
   * Creates a ScalaQuery representing the given named query with the given
   * parameters set. This can be used if you want to use some of the
   * extended functionality of ScalaQuery, such as pagination or using
   * query hints. An example would be:
   *
   * <p>
   * <code>
   *   val query = EM.createNamedQuery[Book]("findAllBooks")
   *   query.setFirstResult(20).setMaxResults(100)
   * </code>
   * </p>
   *
   * @param queryName The name of the query to execute
   * @param params Zero or more pairs of (paramName,paramValue)
   *
   * @return The created ScalaQuery[A]
   */
  def createNamedQuery[A](queryName : String, params : Pair[String,Any]*) : ScalaQuery[A] = createAndParamify[A](queryName,params)

  /*
   * Worker for the previous two methods to handle creating
   * the query and then setting the parameters.
   */
  private def createAndParamify[A](queryName : String, params : Seq[Pair[String,Any]]) : ScalaQuery[A] = {
    val q = createNamedQuery[A](queryName)
    params.foreach(param => q.setParameter(param._1, param._2))
    q
  }

  // Common enough to combine into one op
  
  /**
   * Handles a persist and flush in a single method. This is useful if your
   * persist should check for constraint violations, since otherwise the
   * persist won't occur until the transaction closes.
   *
   * @param entity The entity to persist.
   */
  def persistAndFlush(entity : AnyRef) = { em.persist(entity); em.flush() }

  /**
   * Handles a merge and flush in a single method. This is useful if your
   * merge should check for constraint violations, since otherwise the
   * merge won't occur until the transaction closes.
   *
   * @param entity The entity to merge.
   *
   * @return A newly merged copy of the entity. The original entity should
   * be discarded.
   */
  def mergeAndFlush[T](entity : T) : T = {val e = merge(entity); em.flush(); e}

  /**
   * Handles a remove and flush in a single method. This is useful if your
   * remove should check for constraint violations, since otherwise the
   * remove won't occur until the transaction closes.
   *
   * @param entity The entity to remove.
   */
  def removeAndFlush(entity : AnyRef) = { em.remove(entity); em.flush() }

  // methods defined on Entity Manager
  
  /**
   * Persists the given entity.
   * @param entity The entity to persist
   */
  def persist(entity: AnyRef) = em.persist(entity)

  /**
   * Merges the given entity and returns the newly merged copy.
   * @param entity The entity to merge
   * @return The newly merged copy
   */
  def merge[T](entity: T): T = em.merge(entity)

  /**
   * Removes the given entity.
   * @param entity The entity to remove
   */
  def remove(entity: AnyRef) = em.remove(entity);

  /**
   * Attempts to load a given entity based on its ID. Returns an Option,
   * with None indicating that no entity exists with the given ID.
   *
  * @param clazz The class of the entity to load
  * @param id The ID of the entity to load
  *
  * @return An Option with the loaded entity (Some) or None to indicate
  * that an entity could not be loaded
  */
  def find[A](clazz: Class[A], id: Any) = Utils.findToOption(em.find[A](clazz, id).asInstanceOf[A])

  /**
   * Forces a flush of the current entity state to the database.
   */
  def flush() = em.flush()

  /**
   * Sets the flush mode for the EntityManager.
   *
   * @param flushModeType The desired flush mode
   * @see javax.persistence.FlushModeType
   */
  def setFlushMode(flushModeType: FlushModeType) = em.setFlushMode(flushModeType)

  /**
   * Reloads a given entity's state from the database, effectively
   * discarding any local changes.
   *
   * @param entity The entity to refresh
   */
  def refresh(entity: AnyRef) = em.refresh(entity)

  /**
   * Returns the current flush mode for the EntityManager
   * 
   * @see javax.persistence.FlushModeType
   */
  def getFlushMode() = em.getFlushMode()

  /**
   * Creates a new ScalaQuery[A] using the given query string. The results
   * of the query should be of type A. See
   * <a href="http://www.hibernate.org/hib_docs/entitymanager/reference/en/html/queryhql.html">http://www.hibernate.org/hib_docs/entitymanager/reference/en/html/queryhql.html</a> for an EJB-QL reference.
   *
   * @param queryString The query string (EJBQL)
   * @return A new ScalaQuery[A] that uses the given query string
   */
  def createQuery[A](queryString: String) = new ScalaQuery[A](em.createQuery(queryString))

  /**
   * Creates a new ScalaQuery[A] using the given named query. The results
   * of the named query should be of type A.
   *
   * @param queryName The named query to use
   * @return A new ScalaQuery[A] that uses the given named query
   */
  def createNamedQuery[A](queryName: String) = new ScalaQuery[A](em.createNamedQuery(queryName))

  /**
   * Creates a new ScalaQuery[A] using the given native SQL query. For more
   * details on using native queries, see <a href="http://www.hibernate.org/hib_docs/entitymanager/reference/en/html/query_native.html">http://www.hibernate.org/hib_docs/entitymanager/reference/en/html/query_native.html</a>.
   *
   * @param sqlString The query string to use
   * @return A new ScalaQuery[A] that uses the given native SQL
   */
  def createNativeQuery[A](sqlString: String) = new ScalaQuery[A](em.createNativeQuery(sqlString))

  /**
   * Creates a new ScalaQuery[A] using the given native SQL query. For more
   * details on using native queries, see <a href="http://www.hibernate.org/hib_docs/entitymanager/reference/en/html/query_native.html">http://www.hibernate.org/hib_docs/entitymanager/reference/en/html/query_native.html</a>.
   *
   * @param sqlString The query string to use
   * @param clazz The class of the returned entities
   * @return A new ScalaQuery[A] that uses the given native SQL
   */
  def createNativeQuery[A](sqlString: String, clazz: Class[A]) = new ScalaQuery[A](em.createNativeQuery(sqlString, clazz))

  /**
   * Creates a new ScalaQuery[A] using the given native SQL query. For more
   * details on using native queries, see <a href="http://www.hibernate.org/hib_docs/entitymanager/reference/en/html/query_native.html">http://www.hibernate.org/hib_docs/entitymanager/reference/en/html/query_native.html</a>.
   *
   * @param sqlString The query string to use
   * @param resultSetMapping The name of the mapping from the result set to entities
   * @return A new ScalaQuery[A] that uses the given native SQL
   */
  def createNativeQuery[A](sqlString: String, resultSetMapping: String) = new ScalaQuery[A](em.createNativeQuery(sqlString, resultSetMapping))

  /**
   * Closes the EntityManager. Subclasses may override this if they
   * desire different behavior.
   */
  def close() = factory.closeEM(em)

  /**
   * Returns a boolean indicating whether the EntityManager is open.
   * @return <code>true</code> if the EntityManager is open, <code>false</code> otherwise
   */
  def isOpen() = em.isOpen()

  /**
   * Returns the current EM's transaction. Note that this will throw
   * an exception if you're using JTA for transactions.
   *
   * @return The current transaction
   */
  def getTransaction() = em.getTransaction()

  /**
   * Joins the EM to the currently open JTA transaction. Note that this
   * throws and exception if no JTA transaction exists.
   */
  def joinTransaction() = em.joinTransaction()

  /**
   * Clears the persistence context. Changes made to any entities that have
   * not been flushed to the database will not be persisted.
   */
  def clear() = em.clear()

  /**
   * Returns the underlying provider object for the EntityManager.
   * Implementation-specific.
   *
   * @return The underlying provider
   */
  def getDelegate() = em.getDelegate()

  /**
   * Get an instance, whose state may be lazily fetched. An
   * EntityNotFoundException will be thrown when the instance is accessed
   * if no corresponding instance exists in the database. The exception
   * may be thrown when getReference is called.
   *
   * @param clazz The class of the instance
   * @param primaryKey The primary key of the instance
   *
   * @return A lazily fetched instance
   */
  def getReference[A](clazz: Class[A], primaryKey: Any) = em.getReference[A](clazz, primaryKey)

  /**
   * Locks the given entity using the given lock mode.
   *
   * @param entity The entity to lock
   * @param lockMode The mode of the lock
   *
   * @see javax.persistence.LockModeType
   */
  def lock(entity: AnyRef, lockMode: LockModeType) = em.lock(entity, lockMode)

  /**
   * Tests to see if the given entity is attached to the current
   * EntityManager
   *
   * @param entity The entity to check
   * @return <code>true</code> if attached, <code>false</code> otherwise
   */
  def contains(entity: AnyRef) = em.contains(entity)
}
