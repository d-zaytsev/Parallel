package org.example.trees

import org.example.nodes.OptimisticNode

class OptimisticTree<K : Comparable<K>, V> : AbstractTree<K, V, OptimisticNode<K, V>>() {
    override suspend fun search(key: K): V? {
        TODO("Not yet implemented")
    }

    override suspend fun add(key: K, value: V) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(key: K) {
        TODO("Not yet implemented")
    }
}