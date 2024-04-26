import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.trees.HardSyncTree
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.annotations.StateRepresentation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class HardSyncTest {

    @Test
    fun `Parallel adding`() {

        val elementsCount = 1000
        val tree = HardSyncTree<Int, String>()

        // Make a tree with (3 * elementsCount) size
        runBlocking {
            repeat(elementsCount) {
                launch {
                    delay(Random.nextLong(100))
                    tree.add(it, "test el")
                }

                launch {
                    delay(Random.nextLong(100))
                    tree.add(elementsCount + it, "test el")
                }

                launch {
                    delay(Random.nextLong(100))
                    tree.add(elementsCount * 2 + it, "test el")
                }
            }
        }

        runBlocking {
            for (i in 0 until elementsCount * 3) {
                assertEquals(tree.search(i), "test el")
            }
        }

    }

    @Test
    fun `Parallel tree nodes removing`() {

        val elementsCount = 1000
        val tree = HardSyncTree<Int, String>()

        runBlocking {
            repeat(elementsCount * 3) {
                tree.add(it, "el")
            }
        }

        // Make a tree with (3 * elementsCount) size
        runBlocking {
            repeat(elementsCount) {
                launch {
                    delay(Random.nextLong(100))
                    tree.remove(it)
                }

                launch {
                    delay(Random.nextLong(100))
                    tree.remove(elementsCount + it)
                }

                launch {
                    delay(Random.nextLong(100))
                    tree.remove(elementsCount * 2 + it)
                }
            }
        }

        runBlocking {
            for (i in 0 until elementsCount * 3)
                assertEquals(tree.search(i), null)

        }

    }

    /*
    Remove all nodes except several random nodes
     */
    @Test
    fun `Parallel tree nodes removing #2`() {

        val elementsCount = 1000
        val tree = HardSyncTree<Int, Int>()

        runBlocking {
            repeat(elementsCount * 3) {
                tree.add(it, it)
            }
        }

        // Elements in this range will not be removed
        val notRemoveRange = Random.nextInt(elementsCount)..Random.nextInt(elementsCount + 1, elementsCount * 3)
        print(notRemoveRange)

        // Make a tree with (3 * elementsCount) size
        runBlocking {
            repeat(elementsCount) {
                launch {
                    delay(Random.nextLong(100))
                    if (it !in notRemoveRange)
                        tree.remove(it)

                }

                launch {
                    delay(Random.nextLong(100))
                    val id = elementsCount + it
                    if (id !in notRemoveRange)
                        tree.remove(id)
                }

                launch {
                    delay(Random.nextLong(100))
                    val id = elementsCount * 2 + it
                    if (id !in notRemoveRange)
                        tree.remove(id)
                }
            }
        }

        runBlocking {
            for (i in 0 until elementsCount * 3) {
                if (i in notRemoveRange)
                    assertEquals(tree.search(i), i)
                else
                    assertEquals(tree.search(i), null)

            }
        }

    }


}