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
        if (this.key == key) {
            if (left == null && right == null)
                return null
            else if (left == null)
                return right
            else if (right == null)
                return left
            else {
                val minNode = right?.min() ?: throw NullPointerException()

                this.key = minNode.key
                this.value = minNode.value
                this.right = right?.remove(right ?: throw NullPointerException(), minNode.key)
                return this
            }

        } else {
            if (left == null && right == null)
                throw IllegalArgumentException("Node with key $key doesn't exist")

            if (this.key < key) {
                // we are parent of removing node
                if (right?.key == key) {
                    lock()
                    right?.lock()
                    if (validate(this) && right?.key == key) {
                        val newNode = right?.remove(right ?: throw NullPointerException(), key)
                        right?.unlock()
                        right = newNode
                        unlock()
                    } else {
                        right?.unlock()
                        unlock()
                        throw IllegalThreadStateException()
                    }
                } else right = right?.remove(right ?: throw NullPointerException(), key)

            } else {
                if (left?.key == key) {
                    lock()
                    left?.lock()
                    if (validate(this) && left?.key == key) {
                        val newNode = left?.remove(left ?: throw NullPointerException(), key)
                        left?.unlock()
                        left = newNode
                        unlock()
                    } else {
                        left?.unlock()
                        unlock()
                        throw IllegalThreadStateException()
                    }
                } else left = left?.remove(left ?: throw NullPointerException(), key)

            }
            return subTree
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is OptimisticNode<*, *>) key == other.key && value == other.value && left == other.left && right == other.right
        else false
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}