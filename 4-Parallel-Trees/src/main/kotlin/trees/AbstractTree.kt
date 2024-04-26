package org.example.trees

import org.example.nodes.AbstractNode

abstract class AbstractTree<K : Comparable<K>, V> {
    protected var root: AbstractNode<K, V>? = null

    abstract suspend fun search(key: K): V?
    abstract suspend fun add(key: K, value: V)
    abstract suspend fun remove(key: K)

    override fun toString(): String {
        val sb = StringBuilder()
        root?.buildString(sb)

        return sb.toString()
    }
}