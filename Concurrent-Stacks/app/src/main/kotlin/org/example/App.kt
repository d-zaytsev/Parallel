package org.example

import benchmark.ProduceConsumeBenchmark
import stack.EliminationStack
import stack.TreiberStack
import kotlin.math.roundToInt

fun main() {

    // Stacks settings
    val threadCountArray = arrayOf(1, 2, 4, 8, 14, 32)

    // Tests settings
    val timeArray = arrayOf(1000L, 2000L, 4000L, 8000L)
    val workload = 100L
    val repeats = 5

    println("Treiber`s Stack (without backoff)")

    for (time in timeArray) {
        println("Time: $time")
        for (threadCount in threadCountArray) {
            val bench = ProduceConsumeBenchmark(TreiberStack(), workload)

            val results = Array(repeats) {
                bench.perform(time, threadCount)
            }

            println("threads: $threadCount | result: ${results.average().roundToInt()} ops")
        }
    }

    println("Elimination Backoff Stack")

    for (time in timeArray) {
        println("Time: $time")
        for (threadCount in threadCountArray) {
            val bench = ProduceConsumeBenchmark(EliminationStack(threadCount, threadCount * 100), workload)

            val results = Array(repeats) {
                bench.perform(time, threadCount)
            }

            println("threads: $threadCount | result: ${results.average().roundToInt()} ops")
        }
    }
}
