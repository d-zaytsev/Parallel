package org.example.nodes

import kotlinx.coroutines.sync.Mutex

class SoftNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: SoftNode<K, V>? = null,
    right: SoftNode<K, V>? = null
) : AbstractNode<K, V, SoftNode<K, V>>(key, value, left, right) {

    private val mutex = Mutex()

    suspend fun lock() = mutex.lock()

    fun unlock() = mutex.unlock()

    override suspend fun add(key: K, value: V) {
        TODO("Not yet implemented")
    }

    override suspend fun search(key: K): V? {
        TODO("123")
    }

    override suspend fun remove(root: SoftNode<K, V>, key: K): SoftNode<K, V>? {
        TODO("Not yet implemented")
    }

    override fun min(): SoftNode<K, V>? {
        TODO("Not yet implemented")
    }

}