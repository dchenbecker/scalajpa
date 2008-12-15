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
package org.scala_tools.jpa

import _root_.javax.persistence.EntityManager

/**
 * This trait can be mixed in so that an object may provide
 * access to EntityManager instances on a thread-local basis.
 * An example would be:
 *
 * <code>
 * object MyEM extends LocalEM("test") with ThreadLocalEM;
 * </code>
 *
 * Best practice for this code is to ensure that when the
 * thread exits it calls the cleanup method so that the EM is properly closed.
 */
trait ThreadLocalEM extends ScalaEntityManager with ScalaEMFactory {
  // The actual thread-local store
  private final val cache = new ThreadLocal[EntityManager]() {
    override def initialValue () = openEM()
  }

  // Implemented for ScalaEntityManager
  def em = cache.get()
  val factory = this

  /**
   * Cleans up the current thread's EntityManager by closing it and
   * removing the em from the thread-local storage.
   */
  def cleanup() : Unit = {
    closeEM(em)
    cache.remove()
  }
}
      
  
