package org.example

import kotlinx.coroutines.runBlocking
import org.example.trees.HardSyncTree
import java.util.TreeMap

fun main() {
    runBlocking {
        val tree = HardSyncTree<Int, String>()

        tree.add(5, "root")

        print(tree.search(5))
    }
}