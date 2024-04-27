package org.example.nodes

import kotlinx.coroutines.sync.Mutex

class SoftNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: SoftNode<K, V>? = null,
    right: SoftNode<K, V>? = null
) : AbstractNode<K, V>(key, value, left, right) {

    private val mutex = Mutex()

    suspend fun lock() = mutex.lock()

    fun unlock() = mutex.unlock()

    override suspend fun add(key: K, value: V) {
        TODO("Not yet implemented")
    }

    override suspend fun search(key: K): V? {
        if (this.key == key) {
            val right = (right as SoftNode<K, V>?)
            val left = (left as SoftNode<K, V>?)

            return this.value
        } else if (this.key < key) {
            right?.search(key)
        } else {
            left?.search(key)
        }
    }

    override suspend fun remove(root: AbstractNode<K, V>, key: K): AbstractNode<K, V>? {
        TODO("Not yet implemented")
    }

    override fun min(): AbstractNode<K, V>? {
        TODO("Not yet implemented")
    }

}