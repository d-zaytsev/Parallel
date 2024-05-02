# Binary Tree Implementations (Multithreaded)

This project implements three different concurrent binary trees in Kotlin:

- *Hard synchronization:* The entire data structure is locked during operations;
- *Soft synchronization:* Only the current and previous nodes are blocked;
- *Optimistic synchronization:* a special function *validate()* is used to minimize the number of locks.

Each implementation is designed to run in a multithreaded environment, allowing for concurrent access to the tree.

## Tests

To test this project, the following JUnit-tests were created:

- *Concurrent adding:* A specified number of nodes are added to the tree in parallel. Then occurs a data loss check;
- *Concurrent removing (2 tests):* We creat a tree with a certain number of nodes, then some nodes are removed from it in parallel;
- *Combined test:* We create a small tree and then some nodes are removed and added to it in parallel.

## Results

There were no problems with the tests described above.
