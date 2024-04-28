import kotlinx.coroutines.*
import org.example.nodes.AbstractNode
import org.example.trees.AbstractTree
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.test.assertEquals

/**
 *  Class with general tests for trees
 *  @param treeFactory Create new tree
 */
abstract class GeneralTests<N : AbstractNode<Int, String, N>, T : AbstractTree<Int, String, N>>
    (
    private val treeFactory: () -> T,
    private val nodesCount: Int = 1000
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
        val nodeKeys = randomKeys(nodesCount)

        runBlocking {
            repeat(nodesCount) {
                tree.add(nodeKeys[it], "test")
            }
        }

        // Remove all elements
        runBlocking {
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
        val nodeKeys = randomKeys(nodesCount).toMutableList()

        runBlocking {
            repeat(nodesCount) {
                tree.add(nodeKeys[it], nodeKeys[it].toString())
            }
        }

        // Elements in this list will not be removed
        val notRemove = nodeKeys.shuffled(Random).take(nodesCount.div(10))

        // Remove some elements
        runBlocking {
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

}