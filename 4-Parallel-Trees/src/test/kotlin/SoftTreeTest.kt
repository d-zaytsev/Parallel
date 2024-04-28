import org.example.nodes.HardNode
import org.example.nodes.SoftNode
import org.example.trees.HardTree
import org.example.trees.SoftTree

class SoftTreeTest : GeneralTests<SoftNode<Int, String>, SoftTree<Int, String>>(
    treeFactory = { SoftTree() }
)