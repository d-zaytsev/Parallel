package org.example.trees

import kotlinx.coroutines.sync.Mutex
import org.example.nodes.HardNode
import org.example.nodes.SoftNode

class SoftTree<K : Comparable<K>, V> : AbstractTree<K, V, SoftNode<K, V>>() {
    private val rootMutex = Mutex()

    override suspend fun search(key: K): V? {
        return root?.search(key)
    }

    override suspend fun add(key: K, value: V) {
        rootMutex.lock()

        if (root != null) {
            rootMutex.unlock()
            root?.add(key, value)
        } else {
            root = SoftNode(key, value)
            rootMutex.unlock()
        }
    }

    override suspend fun remove(key: K) {
        TODO("Not yet implemented")
    }
}