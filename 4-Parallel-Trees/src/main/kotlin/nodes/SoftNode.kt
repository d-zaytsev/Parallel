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

    suspend fun lock() = mutex.lock()
    fun unlock() = mutex.unlock()


    override suspend fun add(key: K, value: V) {
        if (this.key == key) {

            this.lock()
            throw IllegalArgumentException("Node with key $key already exists")

        } else if (this.key < key) {

            if (right == null) {
                right = SoftNode(key, value)
                this.unlock()
            } else {
                right?.lock()
                this.lock()
                right?.add(key, value)
            }

        } else {

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
            if (left == null && right == null) {
                this.unlock()
                return null
            } else if (left == null) {
                this.unlock()
                return right
            } else if (right == null) {
                this.unlock()
                return left
            } else {

                right?.lock()
                val minNode = right?.min() ?: throw NullPointerException()
                right = right?.remove(right ?: throw NullPointerException(), minNode.key)

                this.key = minNode.key
                this.value = minNode.value

                this.unlock()
                return this

            }

        } else {
            if (left == null && right == null) {
                this.unlock()
                throw IllegalArgumentException("Node with key $key doesn't exist")
            }

            val childIsRemoving = key == right?.key || key == left?.key

            if (this.key < key) {
                right?.lock()
                if (!childIsRemoving)
                    this.unlock()
                right = right?.remove(right ?: throw NullPointerException(), key)
            } else {
                left?.lock()
                if (!childIsRemoving)
                    this.unlock()
                left = left?.remove(left ?: throw NullPointerException(), key)
            }

            if (childIsRemoving)
                this.unlock()

            return subTree
        }

    }

}