package org.example.trees

import org.example.nodes.AbstractNode

abstract class AbstractTree<K : Comparable<K>, V> {
    protected var root: AbstractNode<K, V>? = null

    abstract fun search(key: K): V?
    abstract fun add(key: K, value: V)
    abstract fun remove(key: K)
}