package org.example.trees

/**
 * Tree node
 */
data class Node<K : Comparable<K>, V>(
    val key: K,
    val value: V,
    val left: Node<K, V>,
    val right: Node<K, V>
)