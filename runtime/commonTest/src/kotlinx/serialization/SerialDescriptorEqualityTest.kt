/*
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization

import kotlinx.serialization.builtins.*
import kotlin.test.*

class SerialDescriptorEqualityTest {
    @Serializable
    class TypeParamUsedOnce<T>(val t: T)

    @Serializable
    class TypeParamUsedTwice<T>(val t: T, val l: List<T>)

    @Serializable
    class TypeParamInList<T>(val l: List<T>)

    @Serializable
    class RecursiveSimple(val desc: String, var node: RecursiveSimple?)

    @Serializable
    class Recursive<T>(val desc: T, var node: Recursive<T>?)

    private fun doTestWith(factory: (KSerializer<*>, KSerializer<*>) -> Pair<SerialDescriptor, SerialDescriptor>) {
        val (a, b) = factory(Int.serializer(), Int.serializer())
        assertEquals(a, b)
        val (c, d) = factory(Int.serializer(), String.serializer())
        assertNotEquals(c, d)
    }

    @Test
    fun testUsedOnce() = doTestWith { d1, d2 ->
        TypeParamUsedOnce.serializer(d1).descriptor to TypeParamUsedOnce.serializer(d2).descriptor
    }

    @Test
    fun testUsedTwice() = doTestWith { d1, d2 ->
        TypeParamUsedTwice.serializer(d1).descriptor to TypeParamUsedTwice.serializer(d2).descriptor
    }

    @Test
    fun testUsedInList() = doTestWith { d1, d2 ->
        TypeParamInList.serializer(d1).descriptor to TypeParamInList.serializer(d2).descriptor
    }

    @Test
    fun testRecursive() = doTestWith { d1, d2 ->
        Recursive.serializer(d1).descriptor to Recursive.serializer(d2).descriptor
    }

    @Test
    fun testRecursiveSimple() {
        val desc = RecursiveSimple.serializer().descriptor
        assertEquals(desc, desc)
        assertNotEquals(desc, Recursive.serializer(String.serializer()).descriptor)
    }
}
