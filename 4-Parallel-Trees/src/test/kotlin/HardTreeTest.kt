import org.example.nodes.HardNode
import org.example.trees.HardTree

class HardTreeTest : GeneralTests<HardNode<Int, String>, HardTree<Int, String>>(
    treeFactory = { HardTree() }
)