package org.example.nodes

class HardSyncNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: HardSyncNode<K, V>? = null,
    right: HardSyncNode<K, V>? = null
) : AbstractNode<K, V>(key, value, left, right) {

    override fun add(key: K, value: V) {
        if (this.key == key) throw IllegalArgumentException("Node with such key already exists")
        else if (this.key < key)
            if (right == null) right = HardSyncNode(key, value) else right?.add(key, value)
        else
            if (left == null) left = HardSyncNode(key, value) else left?.add(key, value)
    }

    override fun search(key: K): V? {
        return if (this.key == key) this.value
        else if (this.key < key) right?.search(key)
        else left?.search(key)
    }

    override fun min(): AbstractNode<K, V>? {
        return if (this.left == null) this
        else this.left?.min()
    }

    override fun remove(root: AbstractNode<K, V>, key: K): AbstractNode<K, V>? {
        if (this.key == key) {
            if (left == null && right == null)
                return null
            else if (left == null)
                return right
            else if (right == null)
                return left
            else {
                val minNode = min() ?: throw NullPointerException()

                this.key = minNode.key
                this.value = minNode.value
                this.left = left?.remove(left!!, minNode.key)
                return this
            }

        } else {
            if (left == null && right == null)
                throw IllegalArgumentException("Node with key $key doesn't exist")

            if (key < this.key)
                this.left = left?.remove(left!!, key)
            else
                this.right = right?.remove(right!!, key)
            return root
        }

    }
}