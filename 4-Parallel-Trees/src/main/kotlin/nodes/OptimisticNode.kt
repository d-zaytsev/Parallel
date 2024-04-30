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

    private suspend fun lock() = mutex.lock()
    private fun unlock() = mutex.unlock()

    override suspend fun add(key: K, value: V) {
        if (this.key == key) throw IllegalArgumentException("Node with key $key already exists")
        else if (this.key < key) {
            // move to the right subtree
            if (right == null) {
                // add right node
                lock()
                // check current node existence & null of right neighbour
                if (validate(this) && right == null) {
                    // current node still exists
                    right = OptimisticNode(key, value, validate = validate)
                    unlock()
                } else {
                    // the node no longer exists, report an error
                    unlock()
                    throw IllegalThreadStateException()
                }
            } else right?.add(key, value)
        } else {
            // move to the left subtree
            if (left == null) {
                // add left node
                lock()
                // check current node existence & null of left neighbour
                if (validate(this) && left == null) {
                    // current node still exists
                    left = OptimisticNode(key, value, validate = validate)
                    unlock()
                } else {
                    // the node no longer exists, report an error
                    unlock()
                    throw IllegalThreadStateException()
                }
            } else left?.add(key, value)
        }
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
        return if (other is OptimisticNode<*, *>) key == other.key && value == other.value && left == other.left && right == other.right
        else false
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}