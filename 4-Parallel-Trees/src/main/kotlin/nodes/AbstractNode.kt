package org.example.nodes

/**
 * Tree node
 */
abstract class AbstractNode<K : Comparable<K>, V>(
    var key: K,
    var value: V,
    var left: AbstractNode<K, V>? = null,
    var right: AbstractNode<K, V>? = null
) {
    abstract fun add(key: K, value: V)
    abstract fun search(key: K): V?
    abstract fun remove(root: AbstractNode<K, V>, key: K): AbstractNode<K, V>?
    abstract fun min(): AbstractNode<K, V>?
}