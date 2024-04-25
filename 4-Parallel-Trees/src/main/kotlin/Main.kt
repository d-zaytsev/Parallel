package org.example

import org.example.trees.HardSyncTree

fun main() {
    val tree = HardSyncTree<Int, String>()

    tree.add(5, "")
    tree.add(10, "2")
    tree.add(4, "")
    tree.add(15, "")
    tree.add(11, "11")

    tree.remove(5)

    print(tree.search(11))
}