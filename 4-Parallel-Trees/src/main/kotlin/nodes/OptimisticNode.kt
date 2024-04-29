package org.example.nodes

import kotlinx.coroutines.sync.Mutex

class OptimisticNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: OptimisticNode<K, V>? = null,
    right: OptimisticNode<K, V>? = null,
    private val validate: (OptimisticNode<K, V>) -> Boolean
) : AbstractNode<K, V, OptimisticNode<K, V>>(key, value, left, right) {
    private val mutex = Mutex()

    suspend fun lock() = mutex.lock()
    fun unlock() = mutex.unlock()

    override suspend fun add(key: K, value: V) {
        if (this.key == key) throw IllegalArgumentException("Node with key $key already exists")
        else if (this.key < key)
            if (right == null) {
                this.lock()
                if (validate(this)) {
                    right = OptimisticNode(key, value, validate = validate)
                    this.unlock()
                } else {
                    this.unlock()
                    throw IllegalThreadStateException()
                }
            } else right?.add(key, value)
        else
            if (left == null) {
                this.lock()
                if (validate(this)) {
                    left = OptimisticNode(key, value, validate = validate)
                    this.unlock()
                } else {
                    this.unlock()
                    throw IllegalThreadStateException()
                }
            } else left?.add(key, value)
    }

    override suspend fun search(key: K): V? {
        return if (this.key == key) {
            this.lock()
            if (validate(this)) {
                this.unlock()
                this.value
            } else {
                this.unlock()
                throw IllegalThreadStateException()
            }
        } else if (this.key < key) right?.search(key)
        else left?.search(key)
    }

    override suspend fun remove(subTree: OptimisticNode<K, V>, key: K): OptimisticNode<K, V>? {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        return if (other is OptimisticNode<*, *>)
            key == other.key && value == other.value && left == other.left && right == other.right
        else false
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}