import java.util.*

class SequentialTree {
    private val tree = TreeMap<Int, String>()

    fun add(key: Int, value: String) = tree.put(key, value)

    fun remove(key: Int) = tree.remove(key)

    fun search(key: Int) = tree[key]
}