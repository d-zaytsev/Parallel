package org.example.trees.interfaces

abstract class AbstractTree<K : Comparable<K>, V> {
    protected var root: Node<K, V>? = null

    protected abstract fun find(key: K): Node<K, V>

    abstract fun search(key: K): V
    abstract fun add(key: K, value: V)
    abstract fun remove(key: K)
}