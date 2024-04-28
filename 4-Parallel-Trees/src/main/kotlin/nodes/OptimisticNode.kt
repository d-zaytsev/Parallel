package org.example.nodes

import kotlinx.coroutines.sync.Mutex

class OptimisticNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: OptimisticNode<K, V>? = null,
    right: OptimisticNode<K, V>? = null,
    private val validate: (Pair<OptimisticNode<K, V>, OptimisticNode<K, V>>) -> Boolean
) : AbstractNode<K, V, OptimisticNode<K, V>>(key, value, left, right) {
    val mutex = Mutex()

    override suspend fun add(key: K, value: V) {
        TODO("Not yet implemented")
    }

    override suspend fun search(key: K): V? {
        if (this.key < key) {
            if (right?.key == key) {

                mutex.lock()
                right?.mutex?.lock()

                if (validate(Pair(this, right ?: throw NullPointerException()))) {
                    right?.mutex?.unlock()
                    mutex.unlock()

                    return right?.value ?: throw NullPointerException()
                } else {
                    right?.mutex?.unlock()
                    mutex.unlock()

                    throw IllegalThreadStateException() // signal that tree is broken
                }
            } else {
                return right?.search(key) ?: throw IllegalThreadStateException()
            }
        } else if (this.key > key) {
            if (left?.key == key) {

                mutex.lock()
                left?.mutex?.lock()

                if (validate(Pair(this, left ?: throw NullPointerException()))) {
                    left?.mutex?.unlock()
                    mutex.unlock()

                    return left?.value ?: throw NullPointerException()
                } else {
                    left?.mutex?.unlock()
                    mutex.unlock()

                    throw IllegalThreadStateException() // signal that tree is broken
                }
            } else {
                return left?.search(key) ?: throw IllegalThreadStateException()
            }
        } else {
            throw IllegalStateException()
        }
    }

    override suspend fun remove(subTree: OptimisticNode<K, V>, key: K): OptimisticNode<K, V>? {
        TODO("Not yet implemented")
    }
}