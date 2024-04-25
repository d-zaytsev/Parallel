# Concurrent Stacks
Implementation and comparison of two different concurrent stacks in Kotlin.
## Implementation
Elemination Backoff Stack was implemented and tested according to article "A Scalable Lock-free Stack Algorithm" using Kotlin and object-oriented programming principles.
## Performance test
Testing was carried out on different numbers of threads (1, 2, 4, 8, 14, 32). At the same time, it was measured how many push and pop operations the stack could perform in the allotted time (1 second, 2, 4, 8).

## Experiment conditions
- **OC**: Ubuntu 23.10 mantic x86_64;
- **PC**: VivoBook_ASUSLaptop X513EQN_S5;
- **CPU**: 11th Gen Intel i7-1165G7 (4 cores, 8 threads);
- **Java**: openjdk 17.0.10 2024-01-16.

## Results
[ParallelExperiments1.xlsx](https://github.com/d-zaytsev/Parallel/files/14770044/ParallelExperiments1.xlsx)
- During performance testing, it was not possible to obtain the same results as in the article. The performance of the stacks remained at approximately the same level;
- The efficiency of the Elemination Backoff Stack depends heavily on the input parameters. The most successful ones were selected for the experiments.
