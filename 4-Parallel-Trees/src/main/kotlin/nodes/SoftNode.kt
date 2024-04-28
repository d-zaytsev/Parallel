package org.example.nodes

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SoftNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: SoftNode<K, V>? = null,
    right: SoftNode<K, V>? = null
) : AbstractNode<K, V, SoftNode<K, V>>(key, value, left, right) {

    val mutex = Mutex()

    override suspend fun add(key: K, value: V) {
        if (this.key == key) {

            mutex.unlock()
            throw IllegalArgumentException("Node with key $key already exists")

        } else if (this.key < key) {

            if (right == null) {
                right = SoftNode(key, value)
                mutex.unlock()
            } else {
                right?.mutex?.lock()
                mutex.unlock()
                right?.add(key, value)
            }

        } else {

            if (left == null) {
                left = SoftNode(key, value)
                mutex.unlock()
            } else {
                left?.mutex?.lock()
                mutex.unlock()
                left?.add(key, value)
            }

        }
    }

    override suspend fun search(key: K): V? {
        return if (this.key == key) {
            mutex.unlock()
            this.value
        }
        else if (this.key < key) {
            right?.mutex?.lock()
            mutex.unlock()
            right?.search(key)
        }
        else {
            left?.mutex?.lock()
            mutex.unlock()
            left?.search(key)
        }
    }

    override suspend fun remove(subTree: SoftNode<K, V>, key: K): SoftNode<K, V>? {
        mutex.lock()

        if (this.key == key) {
            if (left == null && right == null) {
                mutex.unlock()
                return null
            } else if (left == null) {
                mutex.unlock()
                return right
            } else if (right == null) {
                mutex.unlock()
                return left
            } else {

                val minNode = right?.min() ?: throw NullPointerException()
                right = right?.remove(right ?: throw NullPointerException(), minNode.key)

                this.key = minNode.key
                this.value = minNode.value

                mutex.unlock()
                return this

            }

        } else {
            if (left == null && right == null) {
                mutex.unlock()
                throw IllegalArgumentException("Node with key $key doesn't exist")
            }

            val childIsRemoving = key == right?.key || key == left?.key
            if (!childIsRemoving)
                mutex.unlock()

            if (this.key < key)
                right = right?.remove(right ?: throw NullPointerException(), key)
            else
                left = left?.remove(left ?: throw NullPointerException(), key)

            if (childIsRemoving)
                mutex.unlock()

            return subTree
        }

    }

}