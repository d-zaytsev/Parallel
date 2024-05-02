package org.example.trees

import org.example.nodes.AbstractNode

abstract class AbstractTree<K : Comparable<K>, V, N : AbstractNode<K, V, N>> {
    protected var root: N? = null

    abstract suspend fun search(key: K): V?
    abstract suspend fun add(key: K, value: V)
    abstract suspend fun remove(key: K)

    override fun toString(): String {
        val sb = StringBuilder()
        root?.buildString(sb)

        return sb.toString()
    }
}