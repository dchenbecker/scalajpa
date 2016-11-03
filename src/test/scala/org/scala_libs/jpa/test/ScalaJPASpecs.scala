/*
 * Copyright 2008-2016 Derek Chen-Becker
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
package test

import org.specs2.mutable.Specification

/**
 * A simple EM defined in our test/resources
 */
object ThreadEM extends LocalEMF("test") with ThreadLocalEM

class ScalaJPASpecs extends Specification {
  sequential

  "The 'test' LocalEMF instance" should {
    val myInstance = new MyItem
    myInstance.id = 42
    myInstance.name = "Fred"
    myInstance.description = "Some guy"

    "Persist a new instance" in {
      ThreadEM.contains(myInstance) mustEqual false

      ThreadEM.persistAndFlush(myInstance)

      ThreadEM.contains(myInstance) mustEqual true
    }

    "Retrieve an instance using a raw query" in {
      val items = ThreadEM.createQuery[MyItem]("FROM MyItem").findAll

      items.size must_== 1
      items(0).name must_== "Fred"
    }

    "Retrieve an instance using a named query" in {
      val items = ThreadEM.findAll[MyItem]("AllItems")

      items.size must_== 1
      items(0).name must_== "Fred"
    }

    "Remove an instance" in {
      ThreadEM.contains(myInstance) mustEqual true

      ThreadEM.removeAndFlush(myInstance)

      ThreadEM.contains(myInstance) mustEqual false
    }
  }
}
