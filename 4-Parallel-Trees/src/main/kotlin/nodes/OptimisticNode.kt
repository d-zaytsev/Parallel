package org.example.nodes

class OptimisticNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: OptimisticNode<K, V>? = null,
    right: OptimisticNode<K, V>? = null
) : AbstractNode<K, V, OptimisticNode<K, V>>(key, value, left, right) {
    override suspend fun add(key: K, value: V) {
        TODO("Not yet implemented")
    }

    override suspend fun search(key: K): V? {
        TODO("Not yet implemented")
    }

    override suspend fun remove(subTree: OptimisticNode<K, V>, key: K): OptimisticNode<K, V>? {
        TODO("Not yet implemented")
    }
}