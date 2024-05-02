package org.example.nodes

import kotlinx.coroutines.sync.Mutex

class OptimisticNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: OptimisticNode<K, V>? = null,
    right: OptimisticNode<K, V>? = null,
    private val validate: (OptimisticNode<K, V>?) -> Boolean
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
                if (right == null) {
                    // current node still exists
                    right = OptimisticNode(key, value, validate = validate)
                    if (!validate(right)) {
                        right = null
                        unlock()
                        throw IllegalThreadStateException()
                    }
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
                if (left == null) {
                    // current node still exists
                    left = OptimisticNode(key, value, validate = validate)

                    if (!validate(left)) {
                        left = null
                        unlock()
                        throw IllegalThreadStateException()
                    }

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
            value
        } else if (this.key < key) right?.search(key)
        else left?.search(key)
    }

    private suspend fun minSubstitution(node: OptimisticNode<K, V>): Pair<K, V> {
        var parentNode: OptimisticNode<K, V> = node.right ?: throw NullPointerException()
        var childNode: OptimisticNode<K, V>? = parentNode.left

        // find min node and its parent
        while (childNode?.left != null) {
            parentNode = childNode

            childNode.let { childNode = it.left ?: throw IllegalThreadStateException() }
        }

        parentNode.lock(); childNode?.lock()

        if (childNode == null && validate(parentNode) && parentNode.left == null) {
            // (start node).right == min node

            val res = Pair(parentNode.key, parentNode.value) // copy parent node

            if (parentNode.right != null)
                node.right = parentNode.right // smart substitute
            else
                node.right = null // remove parent node

            parentNode.unlock()
            return res
        } else if (childNode != null && validate(parentNode) && parentNode.left == childNode && childNode?.left == null) {

            val res = Pair(childNode!!.key, childNode!!.value)

            if (childNode!!.right != null) {
                // substitute
                parentNode.left = childNode!!.right
            } else {
                parentNode.left = null
            }

            childNode!!.unlock(); parentNode.unlock()
            return res
        } else {
            childNode?.unlock(); parentNode.unlock()
            throw IllegalThreadStateException()
        }
    }

    override suspend fun remove(subTree: OptimisticNode<K, V>, key: K): OptimisticNode<K, V>? {
        return if (this.key == key) {
            if (left == null && right == null)
                null
            else if (left == null)
                right
            else if (right == null)
                left
            else {
                // find min node
                val minNode = minSubstitution(this)

                this.key = minNode.first
                this.value = minNode.second
                this
            }
        } else {
            // we should remove only nodes where this.key == key
            throw IllegalThreadStateException()
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