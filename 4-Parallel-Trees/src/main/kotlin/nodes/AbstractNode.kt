package org.example.nodes

/**
 * Tree node
 */
abstract class AbstractNode<K : Comparable<K>, V>(
    var key: K,
    var value: V,
    var left: AbstractNode<K, V>? = null,
    var right: AbstractNode<K, V>? = null
) {
    abstract fun add(key: K, value: V)
    abstract fun search(key: K): V?
    abstract fun remove(root: AbstractNode<K, V>, key: K): AbstractNode<K, V>?
    abstract fun min(): AbstractNode<K, V>?

    fun buildString(sb: StringBuilder, padding: String = "", pointer: String = "") {
        sb.append(padding)
        sb.append(pointer)
        sb.append("($key,$value)")
        sb.append("\n")

        val paddingBuilder = StringBuilder(padding)
        paddingBuilder.append("│  ")

        val paddingForBoth = paddingBuilder.toString()
        val pointerForRight = "└──"
        val pointerForLeft = if (right != null) "├──" else "└──"

        left?.buildString(sb, paddingForBoth, pointerForLeft)
        right?.buildString(sb, paddingForBoth, pointerForRight)

    }
}