import kotlinx.coroutines.*
import org.example.nodes.AbstractNode
import org.example.trees.AbstractTree
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.random.Random
import kotlin.test.assertEquals

/**
 *  Class with general tests for trees
 *  @param treeFactory Create new tree
 */
abstract class GeneralTests<N : AbstractNode<Int, String, N>, T : AbstractTree<Int, String, N>>
    (
    private val treeFactory: () -> T,
    private val nodesCount: Int = 100000
) {

    /**
     * Random time for delay
     */
    private fun time() = Random.nextLong(100)

    private fun randomKeys(count: Int) = (0 until count).shuffled(Random).take(nodesCount)

    /**
     * Add some elements in parallel
     */
    @Test
    fun `Parallel adding`() {
        val tree: T = treeFactory()
        val nodeKeysToAdd = randomKeys(nodesCount)

        // Fill tree with elements
        runBlocking {
            coroutineScope {
                repeat(nodesCount) {
                    launch(Dispatchers.Default) {
                        delay(time())
                        tree.add(nodeKeysToAdd[it], nodeKeysToAdd[it].toString())
                    }
                }
            }
        }

        runBlocking {
            for (i in nodeKeysToAdd) {
                assertEquals(i.toString(), tree.search(i))
            }
        }

    }

    /**
     * Create tree and remove all nodes in parallel
     */
    @Test
    fun `Parallel tree nodes removing`() {
        val tree = treeFactory()
        var nodeKeys = randomKeys(nodesCount)

        runBlocking {
            repeat(nodesCount) {
                tree.add(nodeKeys[it], nodeKeys[it].toString())
            }
        }

        // Remove all elements
        runBlocking {
            // to remove unevenly
            nodeKeys = nodeKeys.shuffled(Random)
            repeat(nodesCount) {
                launch(Dispatchers.Default) {
                    delay(time())
                    tree.remove(nodeKeys[it])
                }
            }
        }

        runBlocking {
            for (key in nodeKeys)
                assertEquals(null, tree.search(key))
        }
    }

    /**
     * Create tree and remove some nodes (not all) in parallel
     */
    @Test
    fun `Parallel tree nodes removing #2`() {
        val tree = treeFactory()
        var nodeKeys = randomKeys(nodesCount)

        runBlocking {
            repeat(nodesCount) {
                tree.add(nodeKeys[it], nodeKeys[it].toString())
            }
        }

        // Elements in this list will not be removed
        val notRemove = nodeKeys.shuffled(Random).take(nodesCount.div(10))

        // Remove some elements
        runBlocking {
            // to remove unevenly
            nodeKeys = nodeKeys.shuffled(Random)
            repeat(nodesCount) {
                launch(Dispatchers.Default) {
                    delay(time())
                    if (nodeKeys[it] !in notRemove)
                        tree.remove(nodeKeys[it])
                }

            }
        }

        runBlocking {
            for (i in nodeKeys) {
                if (i in notRemove)
                    assertEquals(i.toString(), tree.search(i))
                else
                    assertEquals(null, tree.search(i))

            }
        }

    }

    /**
     * Create a simple tree and then delete and add nodes to it in parallel
     */
    @Test
    fun `Parallel adding & removing`() {
        val tree = treeFactory()
        val keys = randomKeys(nodesCount)
        // half of elements for start tree
        val half = nodesCount.div(2)
        val startNodes = keys.take(half)
        // nodes to add in parallel
        val newNodes = keys.takeLast(half)

        // Create tree
        runBlocking {
            repeat(half) {
                tree.add(startNodes[it], startNodes[it].toString())
            }
        }

        runBlocking {
            repeat(half) {
                // Add elements
                launch(Dispatchers.Default) {
                    delay(time())
                    tree.add(newNodes[it], "new node")
                }
                // Remove elements
                launch(Dispatchers.Default) {
                    delay(time())
                    tree.remove(startNodes[it])
                }
            }
        }

        runBlocking {
            // all start nodes was deleted
            for (key in startNodes) {
                assertEquals(null, tree.search(key))
            }
            // all new nodes was created
            for (key in newNodes) {
                assertEquals("new node", tree.search(key))
            }
        }
    }

}