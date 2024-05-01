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
        if (root?.key == key) {
            if (!tryRootRemove(key))
                remove(key)
        } else {
            if (root?.right?.key == key) {
                if (!tryChildRemove(root?.right, key))
                    remove(key)
            } else if (root?.left?.key == key) {
                if (!tryChildRemove(root?.left, key))
                    remove(key)
            } else {
                try {
                    root = root?.remove(root ?: throw NullPointerException(), key)
                } catch (_: IllegalThreadStateException) {
                    // if remove fail validating
                    remove(key)
                }
            }
        }
    }

    /**
     * Try to remove root's child
     * @return true - success, false - otherwise
     */
    private suspend fun tryChildRemove(child: OptimisticNode<K, V>?, key: K): Boolean {
        rootMutex.lock()
        child?.lock()
        if (child?.key == key) {
            root = root?.remove(root ?: throw NullPointerException(), key)
            child.unlock()
            rootMutex.unlock()
            return true
        } else {
            child?.unlock()
            rootMutex.unlock()
            return false
        }
    }

    /**
     * Try to remove root node
     * @return true - success, false - otherwise
     */
    private suspend fun tryRootRemove(key: K): Boolean {
        rootMutex.lock()
        if (root?.key == key) {
            // root still exist
            root = null
            rootMutex.unlock()
            return true
        } else {
            // root was deleted
            rootMutex.unlock()
            return false
        }
    }
}