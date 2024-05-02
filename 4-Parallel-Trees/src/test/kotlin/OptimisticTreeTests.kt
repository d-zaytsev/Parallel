import org.example.nodes.OptimisticNode
import org.example.nodes.SoftNode
import org.example.trees.OptimisticTree
import org.example.trees.SoftTree

class OptimisticTreeTests: GeneralTests<OptimisticNode<Int, String>, OptimisticTree<Int, String>>(
    treeFactory = { OptimisticTree() }
)