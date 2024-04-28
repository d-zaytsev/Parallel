package org.example.nodes

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SoftNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: SoftNode<K, V>? = null,
    right: SoftNode<K, V>? = null
) : AbstractNode<K, V, SoftNode<K, V>>(key, value, left, right) {

    private val mutex = Mutex()

    override suspend fun add(key: K, value: V) {
        mutex.lock()

        if (this.key == key) {

            mutex.unlock()
            throw IllegalArgumentException("Node with key $key already exists")

        } else if (this.key < key) {

            if (right == null) {
                right = SoftNode(key, value)
                mutex.unlock()
            } else {
                mutex.unlock()
                right?.add(key, value)
            }

        } else {

            if (left == null) {
                left = SoftNode(key, value)
                mutex.unlock()
            } else {
                mutex.unlock()
                left?.add(key, value)
            }

        }
    }

    override suspend fun search(key: K): V? {
        return if (this.key == key) this.value
        else if (this.key < key) right?.search(key)
        else left?.search(key)
    }

    override suspend fun remove(root: SoftNode<K, V>, key: K): SoftNode<K, V>? {
        TODO("Not yet implemented")
    }

    override fun min(): SoftNode<K, V>? {
        TODO("Not yet implemented")
    }

}