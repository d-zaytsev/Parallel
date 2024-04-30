package org.example.trees

import kotlinx.coroutines.sync.Mutex
import org.example.nodes.OptimisticNode

class OptimisticTree<K : Comparable<K>, V> : AbstractTree<K, V, OptimisticNode<K, V>>() {

    private val rootMutex = Mutex()

    /**
     * We go through the tree (without blocking)
     * @param node the node we are going to
     * @return true if the node exists in the BST, false otherwise
     */
    private fun validate(node: OptimisticNode<K, V>): Boolean {
        var curNode = root ?: throw IllegalStateException("Root can't be null while validating")

        while (curNode != node) {
            curNode = if (curNode.key < node.key)
                curNode.right ?: return false
            else
                curNode.left ?: return false
        }

        return true
    }

    override suspend fun search(key: K): V? {
        return try {
            root?.search(key)
        } catch (_: IllegalThreadStateException) {
            search(key)
        }
    }

    override suspend fun add(key: K, value: V) {
        if (root == null) {
            // Add root
            rootMutex.lock()
            // validating null
            if (root == null) {
                root = OptimisticNode(key, value) { node -> validate(node) }
                rootMutex.unlock()
            } else {
                rootMutex.unlock()
                add(key, value)
            }
        } else {
            // Try to add node
            try {
                root?.add(key, value)
            } catch (_: IllegalThreadStateException) {
                // try again if fail validating
                add(key, value)
            }
        }
    }

    override suspend fun remove(key: K) {
        TODO("Not yet implemented")
    }
}