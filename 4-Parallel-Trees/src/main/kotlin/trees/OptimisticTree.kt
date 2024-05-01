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
        var curNode = root ?: return false

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
        var childNode = root ?: throw IllegalStateException("Root is null")
        var parentNode: OptimisticNode<K, V>? = null

        while (childNode.key != key) {
            parentNode = childNode

            childNode = if (childNode.key < key) {
                val right = childNode.right
                if (right == null && search(key) != null)
                    return remove(key)
                else
                    right ?: throw IllegalStateException("Can't find node with key $key")
            }
            else {
                val left = childNode.left
                if (left == null && search(key) != null)
                    return remove(key)
                else
                    left ?: throw IllegalStateException("Can't find node with key $key")
            }
        }

        if (parentNode == null) {
            // removing root node

            root?.lock()
            if (validate(childNode) && childNode.key == key)
                root?.also { root = it.remove(it, key) }
            else
                remove(key)

        } else {
            // if we found the node and its parent
            parentNode.lock()
            childNode.lock()

            if (validate(childNode) && childNode.key == key) {

                try {
                    if (parentNode.key < key)
                        parentNode.right?.also { parentNode.right = it.remove(it, key) }
                    else
                        parentNode.left?.also { parentNode.left = it.remove(it, key) }
                } catch (e: IllegalArgumentException) {
                    return remove(key)
                }

                parentNode.unlock()
            } else {
                childNode.unlock()
                parentNode.unlock()
                remove(key)
            }
        }

    }
}