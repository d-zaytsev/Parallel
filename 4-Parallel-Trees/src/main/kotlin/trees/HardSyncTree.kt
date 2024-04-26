package org.example.trees

import kotlinx.coroutines.sync.Mutex
import org.example.nodes.HardSyncNode

class HardSyncTree<K : Comparable<K>, V> : AbstractTree<K, V>() {
    private val globalMutex = Mutex()

    override suspend fun search(key: K): V? {
        globalMutex.lock()
        val res = root?.search(key)
        globalMutex.unlock()
        return res
    }

    override suspend fun add(key: K, value: V) {
        globalMutex.lock()
        if (root != null)
            root?.add(key, value)
        else
            root = HardSyncNode(key, value)
        globalMutex.unlock()
    }

    override suspend fun remove(key: K) {
        globalMutex.lock()
        root = root?.remove(root!!, key)
        globalMutex.unlock()
    }

}