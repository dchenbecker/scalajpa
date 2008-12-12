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

import _root_.javax.persistence.NoResultException

object Utils {
  /**
   * Used to handle find quirkiness
   */
  private[jpa] def findToOption[A](f: => A): Option[A] = 
    try {  
      f match {
        case null => None
        case found => Some(found)
      }
    } catch {
      case e: NoResultException => None
    }
}
