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
    private fun validate(node: OptimisticNode<K, V>?): Boolean {
        var curNode = root ?: return false
        node ?: return false

        while (curNode != node) {
            curNode = if (curNode.key < node.key)
                curNode.right ?: return false
            else
                curNode.left ?: return false
        }

        return true
    }

    override suspend fun search(key: K): V? = root?.search(key)

    override suspend fun add(key: K, value: V) {
        if (root == null) {
            // Add root
            rootMutex.lock()
            // validating root
            if (root == null) {
                root = OptimisticNode(key, value) { node -> validate(node) }
                rootMutex.unlock()
            } else {
                add(key, value)
                rootMutex.unlock()
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

        // find child node and parent node
        while (childNode.key != key) {
            parentNode = childNode

            val res = if (childNode.key < key) childNode.right else childNode.left

            if (res == null) {
                if (search(key) != null) {
                    remove(key)
                    return
                } else
                    throw IllegalArgumentException("Can't find node with key $key")
            } else
                childNode = res
        }

        // try to remove child node

        if (parentNode == null) {
            // remove root node

            childNode.lock()

            if (root?.key == childNode.key) {
                try {
                    childNode.also { root = it.remove(it, key) }
                    childNode.unlock()
                } catch (_: IllegalThreadStateException) {
                    // if fail validate
                    childNode.unlock()
                    remove(key)
                    return
                }
            } else {
                childNode.unlock()
                remove(key)
            }

        } else {
            // remove node in tree

            parentNode.lock(); childNode.lock()
            val verify =
                validate(parentNode) && (parentNode.right == childNode || parentNode.left == childNode) && childNode.key == key

            if (verify) {

                // identify the child and remove it
                try {
                    if (parentNode.key < key)
                        childNode.also { parentNode.right = it.remove(it, key) }
                    else
                        childNode.also { parentNode.left = it.remove(it, key) }

                    childNode.unlock(); parentNode.unlock()
                } catch (_: IllegalThreadStateException) {
                    // if fail validate
                    childNode.unlock(); parentNode.unlock()
                    remove(key)
                    return
                }
            } else {
                // if node was changed
                childNode.unlock(); parentNode.unlock()
                remove(key)
            }
        }

    }
}