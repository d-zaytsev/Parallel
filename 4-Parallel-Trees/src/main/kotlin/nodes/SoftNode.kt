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
    private fun unlock() = mutex.unlock()


    override suspend fun add(key: K, value: V) {
        if (this.key == key) {
            this.unlock()
            throw IllegalArgumentException("Node with key $key already exists")
        } else if (this.key < key) {
            // move to the right subtree

            if (right == null) {
                right = SoftNode(key, value)
                this.unlock()
            } else {
                right?.lock()
                this.unlock()
                right?.add(key, value)
            }

        } else {
            // move to the left subtree

            if (left == null) {
                left = SoftNode(key, value)
                this.unlock()
            } else {
                left?.lock()
                this.unlock()
                left?.add(key, value)
            }

        }
    }

    override suspend fun search(key: K): V? {
        return if (this.key == key) {
            this.unlock()
            this.value
        } else if (this.key < key) {
            right?.lock()
            this.unlock()
            right?.search(key)
        } else {
            left?.lock()
            this.unlock()
            left?.search(key)
        }
    }

    override suspend fun remove(subTree: SoftNode<K, V>, key: K): SoftNode<K, V>? {
        if (this.key == key) {
            return if (left == null && right == null) {
                // Remove node without children
                this.unlock()
                null
            } else if (left == null) {
                // Remove node with right child
                this.unlock()
                right
            } else if (right == null) {
                // Remove node with left child
                this.unlock()
                left
            } else {
                // Remove nodes with both children
                right?.lock()
                // find min node from right subtree
                val minNode = right?.min() ?: throw NullPointerException()
                // remove min node from tree
                right = right?.remove(right ?: throw NullPointerException(), minNode.key)

                // change cur node key & value
                this.key = minNode.key
                this.value = minNode.value

                this.unlock()
                this
            }

        } else {
            if (left == null && right == null) {
                this.unlock()
                throw IllegalArgumentException("Node with key $key doesn't exist")
            }

            if (this.key < key) {
                right?.lock()
                this.unlock()
                right = right?.remove(right ?: throw NullPointerException(), key)
            } else {
                left?.lock()
                this.unlock()
                left = left?.remove(left ?: throw NullPointerException(), key)
            }

            return subTree
        }

    }

}