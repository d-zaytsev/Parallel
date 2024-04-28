package org.example.trees

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.nodes.SoftNode

class SoftTree<K : Comparable<K>, V> : AbstractTree<K, V, SoftNode<K, V>>() {
    private val rootMutex = Mutex()

    override suspend fun search(key: K): V? {
        root?.mutex?.lock()
        return root?.search(key)
    }

    override suspend fun add(key: K, value: V) {
        rootMutex.lock()

        if (root != null) {
            root?.mutex?.lock()
            rootMutex.unlock()
            root?.add(key, value)
        } else {
            root = SoftNode(key, value)
            rootMutex.unlock()
        }
    }

    override suspend fun remove(key: K) {
        if (root == key) {
            rootMutex.lock()
            root = root?.remove(root ?: throw NullPointerException(), key)
            rootMutex.unlock()
        } else {
            root?.mutex?.lock()
            root = root?.remove(root ?: throw NullPointerException(), key)
        }
    }
}