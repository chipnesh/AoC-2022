package day6

import noDuplicates
import readInput

fun main() {

    fun firstOccurrence(input: List<String>, packetLength: Int) =
        input
            .first()
            .windowedSequence(packetLength)
            .mapIndexedNotNull { i, s -> s.takeIf(String::noDuplicates)?.run { length + i } }
            .first()

    fun part1(input: List<String>): Int {
        return firstOccurrence(input, 4)
    }

    fun part2(input: List<String>): Int {
        return firstOccurrence(input, 14)
    }


    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}
