package org.example

import kotlinx.coroutines.runBlocking
import org.example.trees.HardTree

fun main() {
    runBlocking {
        val tree = HardTree<Int, String>()

        tree.add(5, "root")

        print(tree.search(5))
    }
}