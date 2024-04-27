import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.nodes.AbstractNode
import org.example.trees.AbstractTree
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals

/**
 *  Class with general tests for trees
 *  @param treeFactory Create new tree
 */
abstract class GeneralTests<N : AbstractNode<Int, String, N>, T : AbstractTree<Int, String, N>>
    (
    private val treeFactory: () -> T,
    private val nodesCount: Int = 100
) {

    @Test
    fun `Parallel adding`() {
        val tree: T = treeFactory()

        // Make a tree with (3 * elementsCount) size
        runBlocking {
            repeat(nodesCount) {
                launch {
                    delay(Random.nextLong(100))
                    tree.add(it, "test el")
                }

                launch {
                    delay(Random.nextLong(100))
                    tree.add(nodesCount + it, "test el")
                }

                launch {
                    delay(Random.nextLong(100))
                    tree.add(nodesCount * 2 + it, "test el")
                }
            }
        }

        runBlocking {
            for (i in 0 until nodesCount * 3) {
                assertEquals(tree.search(i), "test el")
            }
        }

    }

    @Test
    fun `Parallel tree nodes removing`() {
        val tree = treeFactory()

        runBlocking {
            repeat(nodesCount * 3) {
                tree.add(it, "el")
            }
        }

        // Make a tree with (3 * elementsCount) size
        runBlocking {
            repeat(nodesCount) {
                launch {
                    delay(Random.nextLong(100))
                    tree.remove(it)
                }

                launch {
                    delay(Random.nextLong(100))
                    tree.remove(nodesCount + it)
                }

                launch {
                    delay(Random.nextLong(100))
                    tree.remove(nodesCount * 2 + it)
                }
            }
        }

        runBlocking {
            for (i in 0 until nodesCount * 3)
                assertEquals(tree.search(i), null)

        }

    }

    /*
    Remove all nodes except several random nodes
     */
    @Test
    fun `Parallel tree nodes removing #2`() {
        val tree = treeFactory()

        runBlocking {
            repeat(nodesCount * 3) {
                tree.add(it, it.toString())
            }
        }

        // Elements in this range will not be removed
        val notRemoveRange = Random.nextInt(nodesCount)..Random.nextInt(nodesCount + 1, nodesCount * 3)

        // Make a tree with (3 * elementsCount) size
        runBlocking {
            repeat(nodesCount) {
                launch {
                    delay(Random.nextLong(100))
                    if (it !in notRemoveRange)
                        tree.remove(it)

                }

                launch {
                    delay(Random.nextLong(100))
                    val id = nodesCount + it
                    if (id !in notRemoveRange)
                        tree.remove(id)
                }

                launch {
                    delay(Random.nextLong(100))
                    val id = nodesCount * 2 + it
                    if (id !in notRemoveRange)
                        tree.remove(id)
                }
            }
        }

        runBlocking {
            for (i in 0 until nodesCount * 3) {
                if (i in notRemoveRange)
                    assertEquals(tree.search(i), i.toString())
                else
                    assertEquals(tree.search(i), null)

            }
        }

    }

}