package org.example.trees

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
            if (curNode == node) return true

            curNode = if (node.key < curNode.key)
                curNode.left ?: return false
            else
                curNode.right ?: return false
        }

        return false
    }

    override suspend fun search(key: K): V? {
        return root?.search(key)
    }

    override suspend fun add(key: K, value: V) {
        if (root == null) {
            rootMutex.withLock {
                root = OptimisticNode(key, value) { node -> validate(node) }
            }
        } else {
            root?.add(key, value) ?: this.add(key, value)
        }
    }

    override suspend fun remove(key: K) {
        TODO("Not yet implemented")
    }
}