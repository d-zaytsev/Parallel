package org.example.trees

import org.example.nodes.HardSyncNode
import sun.awt.Mutex

class HardSyncTree<K : Comparable<K>, V> : AbstractTree<K, V>() {
    private val globalMutex = Mutex()

    override fun search(key: K): V? {
        globalMutex.lock()
        val res = root?.search(key)
        globalMutex.unlock()
        return res
    }

    override fun add(key: K, value: V) {
        globalMutex.lock()
        if (root != null)
            root?.add(key, value)
        else
            root = HardSyncNode(key, value)
        globalMutex.unlock()
    }

    override fun remove(key: K) {
        globalMutex.lock()
        root = root?.remove(root!!, key)
        globalMutex.unlock()
    }

}