package org.example.nodes

import kotlinx.coroutines.sync.Mutex

class OptimisticNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: OptimisticNode<K, V>? = null,
    right: OptimisticNode<K, V>? = null,
    private val validate: (OptimisticNode<K, V>) -> Boolean
) : AbstractNode<K, V, OptimisticNode<K, V>>(key, value, left, right) {
    val mutex = Mutex()

    override suspend fun add(key: K, value: V) {
        if (this.key == key) throw IllegalArgumentException("Node with key $key already exists")
        else if (this.key < key)
            if (right == null) {
                mutex.lock()
                if (validate(this)) {
                    right = OptimisticNode(key, value, validate = validate)
                    mutex.unlock()
                } else {
                    mutex.unlock()
                    throw IllegalThreadStateException()
                }
            } else right?.add(key, value)
        else
            if (left == null) {
                mutex.lock()
                if (validate(this)) {
                    left = OptimisticNode(key, value, validate = validate)
                    mutex.unlock()
                } else {
                    mutex.unlock()
                    throw IllegalThreadStateException()
                }
            } else left?.add(key, value)
    }

    override suspend fun search(key: K): V? {
        return if (this.key == key) {
            mutex.lock()
            if (validate(this)) {
                mutex.unlock()
                this.value
            } else {
                mutex.unlock()
                throw IllegalThreadStateException()
            }
        } else if (this.key < key) right?.search(key)
        else left?.search(key)
    }

    override suspend fun remove(subTree: OptimisticNode<K, V>, key: K): OptimisticNode<K, V>? {
        TODO("Not yet implemented")
    }
}